package com.method.donuts.service.method;

import com.method.donuts.bos.method.accounts.Account;
import com.method.donuts.bos.method.entities.Entity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class MethodAccountService {

    @Autowired
    private RestTemplate restTemplate;

    public Account createAccount(Account accountToCreate) {

        Account responseAccount = new Account();

        try {
            responseAccount = restTemplate.postForObject("https://demo.methodfi.com/accounts", accountToCreate, Account.class);
        } catch (Exception e) {
            log.error("oops lmao, retrieve Account failed");
            log.error(e.getMessage());
        }

        return responseAccount;
    }

    public List<Account> retrieveAllAccounts(String type) {
        List<Account> AccountList = new ArrayList<>();

        // todo set params for individual vs corporation
        try {
            Account[] accountsResponse = restTemplate.getForObject("https://demo.methodfi.com/accounts", Account[].class);
            if (accountsResponse != null) {
                AccountList = Arrays.asList(accountsResponse);
            }
        } catch (Exception e) {
            log.error("oops lmao, retrieve all entities failed");
            log.error(e.getMessage());
        }

        return AccountList;
    }
    
}
