package com.method.donuts.service;


import com.method.donuts.bos.base.PreuploadResponseBO;
import com.method.donuts.bos.method.accounts.Ach;
import com.method.donuts.bos.method.accounts.Liability;
import com.method.donuts.bos.method.base.MethodObjects;
import com.method.donuts.bos.method.entities.Entity;
import com.method.donuts.bos.method.payments.Payment;
import com.method.donuts.bos.report.xml.PayInfoBO;
import com.method.donuts.entities.AchAccountMapping;
import com.method.donuts.entities.EntityMapping;
import com.method.donuts.entities.LoanAccountMapping;
import com.method.donuts.entities.Reports;
import com.method.donuts.repository.AchAccountRepository;
import com.method.donuts.repository.EntityRepository;
import com.method.donuts.repository.LoanAccountRepository;
import com.method.donuts.repository.ReportsRepository;
import com.method.donuts.service.method.*;
import com.method.donuts.transformer.FileTransformer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Service
@Slf4j
public class FileService {

    @Autowired
    private FileTransformer fileTransformer;

    @Autowired
    private MethodEntityService methodEntityService;

    @Autowired
    private MethodAccountService methodAccountService;

    @Autowired
    private MethodMerchantService methodMerchantService;

    @Autowired
    private MethodPaymentService methodPaymentService;

    @Autowired
    private MethodReportService methodReportService;

    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    private LoanAccountRepository loanAccountRepository;

    @Autowired
    private AchAccountRepository achAccountRepository;

    @Autowired
    private ReportsRepository reportsRepository;

    @Autowired
    private TaskExecutor taskExecutor;


    /*
     * Take payinfoBO pass that to transformer, that will transform all of those into smaller lists of just unique source and destination accounts
     * After that we do a couple different things, first we create all of the stores (source accounts and entities)
     * then we get mappings of all plaid ids -> merchant Ids
     * then we create all of the employees (destination acccounts and entities) (an employee can have any number of accounts)
     * and finally we create all the payment objects with those different mappings
     *
     * the ugly looking part is the quick way to spin up async operations in spring
     */
    public PreuploadResponseBO digestPayInfo(PayInfoBO payInfoBO, String fileName, Boolean preupload) {
        try {
            MethodObjects methodObjects = fileTransformer.xmlToMethodObjects(payInfoBO);
            if(preupload) {
                return fileTransformer.getPreuploadResponseBoFromStoresMap(methodObjects.getStores());
            }
            Map<String, String> storesMap = new HashMap<>();
            Map<String, String> individualsMap = new HashMap<>();
            Map<String, String> storeToAccountMap = new HashMap<>();
            Map<String, String> merchantIdMap = new HashMap<>();
            List<EntityMapping> entityMappings = new ArrayList<>();
            List<LoanAccountMapping> loanAccountMappings = new ArrayList<>();
            List<AchAccountMapping> achAccountMappings = new ArrayList<>();

            // Async stuff
            List<CompletableFuture<AchAccountMapping>> achAccountFutures = new ArrayList<>();
            List<CompletableFuture<Boolean>> merchantIdFutures = new ArrayList<>();
            List<CompletableFuture<Boolean>> paymentsFutures = new ArrayList<>();

            Map<String, List<String>> paymentsMadeMap = new HashMap<>();

            methodObjects.getStores().forEach((dunkinId, corp) -> {
                achAccountFutures.add(CompletableFuture.supplyAsync(() -> createAchAccountForStore(storesMap, storeToAccountMap, entityMappings, dunkinId, corp), taskExecutor));
            });

            methodObjects.getMerchants().forEach((key, value) -> {
                merchantIdFutures.add(CompletableFuture.supplyAsync(() ->  {
                           String merchantId = methodMerchantService.retrieveMerchant(key);
                           if(merchantId != null) {
                               merchantIdMap.put(key, merchantId);
                           }
                           return true;
                        }, taskExecutor));
            });


            CompletableFuture<List<AchAccountMapping>> futureListOfAchAccounts = CompletableFuture.allOf(achAccountFutures.toArray(new CompletableFuture[achAccountFutures.size()])).thenApply(voidArg -> achAccountFutures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList()));
            List<AchAccountMapping> test = futureListOfAchAccounts.get();
            achAccountMappings.addAll(test.stream().filter(Objects::nonNull).collect(Collectors.toList()));

            CompletableFuture<List<Boolean>> futureListMerchantIds = CompletableFuture.allOf(merchantIdFutures.toArray(new CompletableFuture[merchantIdFutures.size()])).thenApply(voidArg -> merchantIdFutures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList()));
            futureListMerchantIds.get();

            methodObjects.getIndividuals().forEach((dunkinId, individual) -> {
                paymentsFutures.add(CompletableFuture.supplyAsync(() -> makePayments(methodObjects, individualsMap, storeToAccountMap, merchantIdMap, entityMappings, loanAccountMappings, paymentsMadeMap, dunkinId, individual), taskExecutor));
            });


            CompletableFuture<List<Boolean>> futureLisPayments = CompletableFuture.allOf(merchantIdFutures.toArray(new CompletableFuture[merchantIdFutures.size()])).thenApply(voidArg -> paymentsFutures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList()));
            futureLisPayments.get();

            if(!entityMappings.isEmpty()) {
                entityRepository.saveAll(entityMappings);
            }
            if(!achAccountMappings.isEmpty()) {
                achAccountRepository.saveAll(achAccountMappings);
            }
            if(!loanAccountMappings.isEmpty()) {
                loanAccountRepository.saveAll(loanAccountMappings);
            }

            String reportId = methodReportService.createReport();

            Reports reports = new Reports(reportId, fileName);

            reportsRepository.save(reports);

            PreuploadResponseBO preuploadResponseBO = new PreuploadResponseBO();
            preuploadResponseBO.setReportId(reportId);
            return preuploadResponseBO;
        } catch (Exception e) {
            // this isn't actually ideal, because an issue could happen on any of the actions, so I'd want to implement some form of safe commits, but I'd consider that out of scope for this
            log.error(e.getMessage());
            e.printStackTrace();
            PreuploadResponseBO preuploadResponseBO = new PreuploadResponseBO();
            preuploadResponseBO.setResponse("Payments failed");
            return preuploadResponseBO;
        }
    }

    // in the interest of time, this is going to unfortunately have to be this ugly. I would likely just make one larger object and would never do this. It's just 2am while I'm cleaning everything up
    /*
     * this function actually does the payments, so taking all of the store account ids, employee loan account ids, and then applying the total of payments to that
     */
    private Boolean makePayments(MethodObjects methodObjects, Map<String, String> individualsMap, Map<String, String> storeToAccountMap, Map<String, String> merchantIdMap, List<EntityMapping> entityMappings, List<LoanAccountMapping> loanAccountMappings, Map<String, List<String>> paymentsMadeMap, String dunkinId, Entity individual) {
        Collection<LoanAccountMapping> foundLoanAccountMapping = loanAccountRepository.findAllByDunkinId(dunkinId);
        if(foundLoanAccountMapping.isEmpty()) {

            String entityId = methodEntityService.createEntity(individual);
            if (entityId != null) {
                individualsMap.put(dunkinId, entityId);
                entityMappings.add(new EntityMapping(dunkinId, entityId, Boolean.TRUE));

                methodObjects.getIndividualAccounts().get(dunkinId).forEach(account -> {
                    account.setHolder_id(entityId);

                    Liability liability = account.getLiability();
                    liability.setMch_id(merchantIdMap.get(liability.getPlaid_id()));
                    String accId = null;
                    if (account.getLiability().getMch_id() != null) {
                        accId = methodAccountService.createAccount(account);
                    }

                    if (accId != null) {
                        loanAccountMappings.add(new LoanAccountMapping(dunkinId, accId, liability.getNumber(), liability.getPlaid_id()));
                    }

                    Map<String, Payment> userPayments = methodObjects.getUserPayments().get(dunkinId).get(new MultiKey<>(account.getLiability().getPlaid_id(), account.getLiability().getNumber()));
                    String finalAccId = accId;
                    userPayments.forEach((dunkinCorpId, payment) -> {
                        if (finalAccId != null) {
                            payment.setDestination(finalAccId);
                            payment.setSource(storeToAccountMap.get(dunkinCorpId));
                            String paymentId = methodPaymentService.createPayment(payment);
                            // this will make for a race condition potentially
                            if (paymentsMadeMap.containsKey(dunkinId)) {
                                paymentsMadeMap.get(dunkinId).add(paymentId);
                            } else {
                                paymentsMadeMap.put(dunkinId, new ArrayList<>(Arrays.asList(paymentId)));
                            }
                        }
                    });
                });
            }
        } else {
            foundLoanAccountMapping.forEach(loanAccountMapping ->  {
                Map<String, Payment> userPayments = methodObjects.getUserPayments().get(dunkinId).get(new MultiKey<>(loanAccountMapping.getPlaidId(), loanAccountMapping.getLoanNumber()));
                // I know this is a duplicate of the one above, and I had a todo here to pull this out, but it's 2:07am
                userPayments.forEach((dunkinCorpId, payment) -> {
                    payment.setDestination(loanAccountMapping.getAccountId());
                    payment.setSource(storeToAccountMap.get(dunkinCorpId));
                    String paymentId = methodPaymentService.createPayment(payment);
                    // this will make for a race condition potentially
                    if (paymentsMadeMap.containsKey(dunkinId)) {
                        paymentsMadeMap.get(dunkinId).add(paymentId);
                    } else {
                        paymentsMadeMap.put(dunkinId, new ArrayList<>(Arrays.asList(paymentId)));
                    }
                });

            });
        }

        return true;
    }

    private AchAccountMapping createAchAccountForStore(Map<String, String> storesMap, Map<String, String> storeToAccountMap, List<EntityMapping> entityMappings, String dunkinId, Entity corp) {
        // puts and adds are thread safe enough for this case, otherwise I'd use a synchronized list
        Collection<AchAccountMapping> foundAchAccountMapping = achAccountRepository.findAllByDunkinId(dunkinId);
        if(foundAchAccountMapping.isEmpty()) {
            String entityId = methodEntityService.createEntity(corp);
            if (entityId != null) {
                entityMappings.add(new EntityMapping(dunkinId, entityId, Boolean.FALSE));

                storesMap.put(dunkinId, entityId);
                corp.getAchAccount().setHolder_id(entityId);

                String accId = methodAccountService.createAccount(corp.getAchAccount());
                storeToAccountMap.put(dunkinId, accId);
                Ach ach = corp.getAchAccount().getAch();

                return new AchAccountMapping(dunkinId, accId, ach.getRouting(), ach.getNumber());
            }
        } else {
            foundAchAccountMapping.forEach(achAccountMapping -> {
                storeToAccountMap.put(dunkinId, achAccountMapping.getAccountId());
            });
        }

        return null;
    }
}
