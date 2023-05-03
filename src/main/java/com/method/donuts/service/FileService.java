package com.method.donuts.service;


import com.method.donuts.bos.base.ResponseDO;
import com.method.donuts.bos.method.accounts.Ach;
import com.method.donuts.bos.method.accounts.Liability;
import com.method.donuts.bos.method.base.MethodObjects;
import com.method.donuts.bos.method.entities.Entity;
import com.method.donuts.bos.method.payments.Payment;
import com.method.donuts.bos.report.xml.PayInfoBO;
import com.method.donuts.entities.AchAccountMapping;
import com.method.donuts.entities.EntityMapping;
import com.method.donuts.entities.LoanAccountMapping;
import com.method.donuts.repository.AchAccountRepository;
import com.method.donuts.repository.EntityRepository;
import com.method.donuts.repository.LoanAccountRepository;
import com.method.donuts.service.method.MethodAccountService;
import com.method.donuts.service.method.MethodEntityService;
import com.method.donuts.service.method.MethodMerchantService;
import com.method.donuts.service.method.MethodPaymentService;
import com.method.donuts.transformer.FileTransformer;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;


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
    private EntityRepository entityRepository;

    @Autowired
    private LoanAccountRepository loanAccountRepository;

    @Autowired
    private AchAccountRepository achAccountRepository;

    public ResponseDO digestPayInfo(PayInfoBO payInfoBO) {
        try {
            // todo setup something for adding a delay
            MethodObjects methodObjects = fileTransformer.xmlToMethodObjects(payInfoBO);
            Map<String, String> storesMap = new HashMap<>();
            Map<String, String> individualsMap = new HashMap<>();
            Map<String, String> storeToAccountMap = new HashMap<>();
            Map<String, String> merchantIdMap = new HashMap<>();
            List<EntityMapping> entityMappings = new ArrayList<>();
            List<LoanAccountMapping> loanAccountMappings = new ArrayList<>();
            List<AchAccountMapping> achAccountMappings = new ArrayList<>();

            // todo this will need to be different, just temporary to make sure things work
            Map<String, List<String>> paymentsMadeMap = new HashMap<>();

            methodObjects.getStores().forEach((dunkinId, corp) -> {
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

                        achAccountMappings.add(new AchAccountMapping(dunkinId, accId, ach.getRouting(), ach.getNumber()));
                    }
                } else {
                    foundAchAccountMapping.forEach(achAccountMapping -> {
                        storeToAccountMap.put(dunkinId, achAccountMapping.getAccountId());
                    });
                }
            });

            methodObjects.getMerchants().forEach((key, value) -> {
                String merchantId = methodMerchantService.retrieveMerchant(key);
                if(merchantId != null) {
                    merchantIdMap.put(key, merchantId);
                }
            });

            methodObjects.getIndividuals().forEach((dunkinId, individual) -> {
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
                            if (account.getLiability().getMch_id() == null) {
                                log.error("this shouldn't happen");
                            }
                            String accId = methodAccountService.createAccount(account);

                            if (accId != null) {
                                loanAccountMappings.add(new LoanAccountMapping(dunkinId, accId, liability.getNumber(), liability.getPlaid_id()));
                            }

                            Map<String, Payment> userPayments = methodObjects.getUserPayments().get(dunkinId).get(new MultiKey<>(account.getLiability().getPlaid_id(), account.getLiability().getNumber()));
                            userPayments.forEach((dunkinCorpId, payment) -> {
                                if (accId == null) {
                                    // todo, they returned a bad merchant, so it's fine
                                    log.error("accId is null");
                                }
                                payment.setDestination(accId);
                                payment.setSource(storeToAccountMap.get(dunkinCorpId));
                                String paymentId = methodPaymentService.createPayment(payment);
                                if (paymentsMadeMap.containsKey(dunkinId)) {
                                    paymentsMadeMap.get(dunkinId).add(paymentId);
                                } else {
                                    paymentsMadeMap.put(dunkinId, new ArrayList<>(Arrays.asList(paymentId)));
                                }
                            });
                        });
                    }
                } else {
                    foundLoanAccountMapping.forEach(loanAccountMapping ->  {
                        Map<String, Payment> userPayments = methodObjects.getUserPayments().get(dunkinId).get(new MultiKey<>(loanAccountMapping.getPlaidId(), loanAccountMapping.getLoanNumber()));
                        userPayments.forEach((dunkinCorpId, payment) -> {
                            payment.setDestination(loanAccountMapping.getAccountId());
                            payment.setSource(storeToAccountMap.get(dunkinCorpId));
                            String paymentId = methodPaymentService.createPayment(payment);
                            if (paymentsMadeMap.containsKey(dunkinId)) {
                                paymentsMadeMap.get(dunkinId).add(paymentId);
                            } else {
                                paymentsMadeMap.put(dunkinId, new ArrayList<>(Arrays.asList(paymentId)));
                            }
                        });

                    });
                }
            });

            if(!entityMappings.isEmpty()) {
                entityRepository.saveAll(entityMappings);
            }
            if(!achAccountMappings.isEmpty()) {
                achAccountRepository.saveAll(achAccountMappings);
            }
            if(!loanAccountMappings.isEmpty()) {
                loanAccountRepository.saveAll(loanAccountMappings);
            }
            log.info("test");

        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
