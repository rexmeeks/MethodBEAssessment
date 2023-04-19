package com.method.donuts.transformer;

import com.method.donuts.bos.method.accounts.Account;
import com.method.donuts.bos.method.accounts.Ach;
import com.method.donuts.bos.method.accounts.Liability;
import com.method.donuts.bos.method.entities.Address;
import com.method.donuts.bos.method.entities.Corporation;
import com.method.donuts.bos.method.entities.Entity;
import com.method.donuts.bos.method.entities.Individual;
import com.method.donuts.bos.method.payments.Payment;
import com.method.donuts.bos.report.xml.EmployeeBO;
import com.method.donuts.bos.report.xml.PayInfoBO;
import com.method.donuts.bos.report.xml.PayeeBO;
import com.method.donuts.bos.report.xml.PayorBO;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FileTransformer {

    // todo will need to set a lot of the ids for the account and loan after creation
    public List<List<Object>> xmlToMethodObjects(PayInfoBO payInfoBO) {
        if (payInfoBO == null ) {
            return new ArrayList<>(new ArrayList<>());
        }

        ArrayList<Entity> entities = new ArrayList<>();
        ArrayList<Account> accounts = new ArrayList<>();
        Map<String, Entity> stores = new HashMap<>();
        // todo will probably need to do a list of lists where item 1 is x, item 2 is y

        ArrayList<Triple<Entity, Payment, String>> triples = payInfoBO.getRow().stream().map((rowBO -> {
            EmployeeBO employeeBO = rowBO.getEmployee();
            PayorBO payorBO = rowBO.getPayor();
            PayeeBO payeeBO = rowBO.getPayee();

            Entity individualEntity = new Entity();
            Individual individual = new Individual();
            Account loanAccount = new Account();
            Liability loanLiability = new Liability();
            Payment payment = new Payment();
            String dunkinCorpId = payorBO.getDunkinId();

            individual.setFirst_name(employeeBO.getFirstName());
            individual.setLast_name(employeeBO.getLastName());
            individual.setPhone(employeeBO.getPhoneNumber());
            individual.setDob(employeeBO.getDateOfBirth());

            loanLiability.setNumber(payeeBO.getLoanAccountNumber());

            loanAccount.setLiability(loanLiability);
            individualEntity.setIndividual(individual);
            individualEntity.setLiability(loanAccount);


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
            return Triple.of(individualEntity, payment, dunkinCorpId);
        })).collect(Collectors.toCollection(ArrayList::new));

        return new ArrayList<>(new ArrayList<>());
    }
}
