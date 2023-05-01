package com.method.donuts.transformer;

import com.method.donuts.bos.method.accounts.Account;
import com.method.donuts.bos.method.accounts.Ach;
import com.method.donuts.bos.method.accounts.Liability;
import com.method.donuts.bos.method.base.MethodObjects;
import com.method.donuts.bos.method.entities.Address;
import com.method.donuts.bos.method.entities.Corporation;
import com.method.donuts.bos.method.entities.Entity;
import com.method.donuts.bos.method.entities.Individual;
import com.method.donuts.bos.method.payments.Payment;
import com.method.donuts.bos.report.xml.EmployeeBO;
import com.method.donuts.bos.report.xml.PayInfoBO;
import com.method.donuts.bos.report.xml.PayeeBO;
import com.method.donuts.bos.report.xml.PayorBO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FileTransformer {

    // todo will need to set a lot of the ids for the account and loan after creation
    public MethodObjects xmlToMethodObjects(PayInfoBO payInfoBO) {
        if (payInfoBO == null ) {
            return new MethodObjects();
        }

        Map<String, Entity> stores = new HashMap<>();
        Map<String, Entity> individuals = new HashMap<>();
        Map<String, List<Account>> individualAccounts = new HashMap<>();
        Map<String, Map<MultiKey<String>, Payment>> userPayments = new HashMap<>();

        Map<String, String> merchants = new HashMap<>();

        // todo set metadata here?

        // I gotta return a lot of shit, so I just probably make an object that holds it all

        payInfoBO.getRow().stream().forEach((rowBO -> {
            EmployeeBO employeeBO = rowBO.getEmployee();
            PayorBO payorBO = rowBO.getPayor();
            PayeeBO payeeBO = rowBO.getPayee();

            Entity individualEntity = new Entity();
            Individual individual = new Individual();
            Account loanAccount = new Account();
            Liability loanLiability = new Liability();
            String dunkinCorpId = payorBO.getDunkinId();
            String dunkinIndividualId = employeeBO.getDunkinId();

            Payment payment = new Payment();

            setCorportation(stores, payorBO, dunkinCorpId);

            MultiKey<String> multiKey = new MultiKey<>(payeeBO.getPlaidId(), payeeBO.getLoanAccountNumber(), payorBO.getDunkinId());

            //todo this could maybe not work, set to getIfPresent
            // error handle this shit
            payment.setAmount(Float.parseFloat(rowBO.getAmount().substring(1)));

            if(payment.getAmount() == null) {
                log.warn("this shouldn't happen");
            }

            loanLiability.setNumber(payeeBO.getLoanAccountNumber());
            loanLiability.setPlaid_id(payeeBO.getPlaidId());
            merchants.putIfAbsent(payeeBO.getPlaidId(), "");
            loanAccount.setLiability(loanLiability);

            if(individuals.containsKey(dunkinIndividualId)) {
                if(!individuals.get(dunkinIndividualId).liabilities.containsKey(payeeBO.getLoanAccountNumber())) {
                    individuals.get(dunkinIndividualId).getLiabilities().put(payeeBO.getLoanAccountNumber(), loanAccount);
                }
            } else {
                individual.setFirst_name(employeeBO.getFirstName());
                individual.setLast_name(employeeBO.getLastName());
                individual.setPhone("15121231111");
                // todo this will need some error handling to ignore shitty dates
                individual.setDob(LocalDate.parse(employeeBO.getDateOfBirth(), DateTimeFormatter.ofPattern("MM-dd-yyyy")).toString());
                individualEntity.setIndividual(individual);
                individualEntity.setType("individual");
                individualEntity.getLiabilities().put(payeeBO.getLoanAccountNumber(), loanAccount);

                individuals.put(dunkinIndividualId, individualEntity);

            }

            if(individualAccounts.containsKey(dunkinIndividualId)) {
                individualAccounts.get(dunkinIndividualId).add(loanAccount);
            } else {
                individualAccounts.put(dunkinIndividualId, new ArrayList<>(Arrays.asList(loanAccount)));
            }

            if(userPayments.containsKey(dunkinIndividualId)) {
                if (userPayments.get(dunkinIndividualId).containsKey(multiKey)) {
                    userPayments.get(dunkinIndividualId).get(multiKey).setAmount(Float.parseFloat(rowBO.getAmount().substring(1)) + userPayments.get(dunkinIndividualId).get(multiKey).getAmount());
                } else {
                    userPayments.get(dunkinIndividualId).put(multiKey, payment);
                }
            } else {
                Map<MultiKey<String>, Payment> newUserPaymentMap = new HashMap<>();
                newUserPaymentMap.put(multiKey, payment);
                userPayments.put(dunkinIndividualId, newUserPaymentMap);
                // this looks sketchy, should do something about this, put it in another method
                // theoretically if a user exists there will exist a payment map
            }
        }));

        return new MethodObjects(stores, individuals, individualAccounts, userPayments, merchants);

    }

    private void setCorportation(Map<String, Entity> stores, PayorBO payorBO, String dunkinCorpId) {
        if (!stores.containsKey(dunkinCorpId)) {
            Entity corporationEntity = new Entity();
            Corporation corporation = new Corporation();
            Address corporationAddress = new Address();
            Account achAccount = new Account();
            Ach ach = new Ach();

            corporation.setDba(payorBO.getDba());
            corporation.setName(payorBO.getName());
            corporation.setEin(payorBO.getEin());

            // todo make sure to do null checks since this shit doesn't have ternary
            if (payorBO.getAddress() != null) {
                corporationAddress.setCity(payorBO.getAddress().getCity());
                corporationAddress.setLine1(payorBO.getAddress().getLine1());
                corporationAddress.setLine2(payorBO.getAddress().getLine2());
                corporationAddress.setState(payorBO.getAddress().getState());
                corporationAddress.setZip(payorBO.getAddress().getZip());
            }

            ach.setRouting(payorBO.getABARouting());
            ach.setNumber(payorBO.getAccountNumber());
            ach.setType("checking");
            achAccount.setAch(ach);

            corporationEntity.setAchAccount(achAccount);
            corporationEntity.setCorporation(corporation);
            corporationEntity.setAddress(corporationAddress);
            corporationEntity.setType("c_corporation");
            stores.put(payorBO.getDunkinId(), corporationEntity);
        }
    }
}
