package com.method.donuts.service.method;

import com.method.donuts.bos.method.accounts.Account;
import com.method.donuts.bos.method.accounts.AccountData;
import com.method.donuts.bos.method.entities.EntityData;
import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

    @Value("${api-key}")
    private String apiKey;

    @Autowired
    private Bucket bucket;

    public String createAccount(Account accountToCreate) {

        AccountData accountData;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Account> request = new HttpEntity<>(accountToCreate, headers);

        try {
            bucket.asBlocking().consume(1);
            accountData = restTemplate.postForObject("https://dev.methodfi.com/accounts", request, AccountData.class);
        } catch(InterruptedException e) {
            log.error("bucket blocking was interuppted, but probably shouldn't happen");
            log.error(e.getMessage());
            return null;
        } catch (Exception e) {
            // todo add interupted exception and more specific exception
            log.error("oops lmao, retrieve entity failed");
            log.error(e.getMessage());
            return null;
        }

        if(accountData != null && !accountData.getData().isEmpty() && accountData.getData().get(0) != null) {
            return accountData.getData().get(0).getId();
        }

        return null;
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
