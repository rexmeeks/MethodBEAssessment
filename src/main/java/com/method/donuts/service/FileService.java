package com.method.donuts.service;


import com.method.donuts.bos.base.ResponseDO;
import com.method.donuts.bos.method.base.MethodObjects;
import com.method.donuts.bos.method.entities.Entity;
import com.method.donuts.bos.method.payments.Payment;
import com.method.donuts.bos.report.xml.PayInfoBO;
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

    public ResponseDO digestPayInfo(PayInfoBO payInfoBO) {
        try {

            // todo setup something for adding a delay
            MethodObjects methodObjects = fileTransformer.xmlToMethodObjects(payInfoBO);
            Map<String, String> storesMap = new HashMap<>();
            Map<String, String> individualsMap = new HashMap<>();
            Map<String, String> storeToAccountMap = new HashMap<>();
            Map<String, String> merchantIdMap = new HashMap<>();
            // todo this will need to be different, just temporary to make sure things work
            Map<String, List<String>> paymentsMadeMap = new HashMap<>();

            methodObjects.getStores().forEach((key, value) -> {
                String entityId = methodEntityService.createEntity(value);
                if(entityId != null) {
                    storesMap.put(key, entityId);
                    value.getAchAccount().setHolder_id(entityId);
                    storeToAccountMap.put(key, methodAccountService.createAccount(value.getAchAccount()));
                }
            });

            methodObjects.getMerchants().forEach((key, value) -> {
                String merchantId = methodMerchantService.retrieveMerchant(key);
                if(merchantId != null) {
                    merchantIdMap.put(key, merchantId);
                }
            });

            methodObjects.getIndividuals().forEach((dunkinId, individual) -> {
                String entityId = methodEntityService.createEntity(individual);
                if(entityId != null) {
                    individualsMap.put(dunkinId, entityId);
                    methodObjects.getIndividualAccounts().get(dunkinId).forEach(account -> {
                        account.setHolder_id(entityId);
                        account.getLiability().setMch_id(merchantIdMap.get(account.getLiability().getPlaid_id()));
                        if(account.getLiability().getMch_id() == null) {
                            log.error("this shouldn't happen");
                        }
                        String accId = methodAccountService.createAccount(account);
                        Map<String, Payment> userPayments = methodObjects.getUserPayments().get(dunkinId).get(new MultiKey<>(account.getLiability().getPlaid_id(), account.getLiability().getNumber()));
                        userPayments.forEach((dunkinCorpId, payment) -> {
                            if(accId == null) {
                                // todo, they returned a bad merchant, so it's fine
                                log.error("accId is null");
                            }
                            payment.setDestination(accId);
                            payment.setSource(storeToAccountMap.get(dunkinCorpId));
                            String paymentId = methodPaymentService.createPayment(payment);
                            if(paymentsMadeMap.containsKey(dunkinId)) {
                                paymentsMadeMap.get(dunkinId).add(paymentId);
                            } else {
                                paymentsMadeMap.put(dunkinId, new ArrayList<>(Arrays.asList(paymentId)));
                            }
                        });
                    });
                }
            });

            log.info("test");

        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
