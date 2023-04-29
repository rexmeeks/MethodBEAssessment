package com.method.donuts.bos.method.base;

import com.method.donuts.bos.method.accounts.Account;
import com.method.donuts.bos.method.entities.Entity;
import com.method.donuts.bos.method.payments.Payment;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.keyvalue.MultiKey;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class MethodObjects {


    private Map<String, Entity> stores;
    private Map<String, Entity> individuals;
    private Map<String, List<Account>> individualAccounts;
    private Map<String, Map<MultiKey<String>, Payment>> userPayments;

    public MethodObjects(){

    }

    public MethodObjects(Map<String, Entity> stores, Map<String, Entity> individuals, Map<String, List<Account>> individualAccounts, Map<String, Map<MultiKey<String>, Payment>> userPayments) {
        this.stores = stores;
        this.individuals = individuals;
        this.individualAccounts = individualAccounts;
        this.userPayments = userPayments;
    }
}
