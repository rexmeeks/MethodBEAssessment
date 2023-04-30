package com.method.donuts.service.method;

import com.method.donuts.bos.method.entities.Entity;
import com.method.donuts.bos.method.merchants.Merchant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
@Slf4j
public class MethodMerchantService {

    @Autowired
    private RestTemplate restTemplate;

    public Merchant retrieveMerchant(String id) {
        Merchant merchant = new Merchant();

        // todo set params for merchant id
        try {
            merchant = restTemplate.getForObject("https://demo.methodfi.com/merchants/", Merchant.class);
        } catch (Exception e) {
            log.error("oops lmao, retrieve all entities failed");
            log.error(e.getMessage());
        }

        return merchant;
    }


    public List<Merchant> retrieveAllEntities(String type) {
        List<Merchant> entityList = new ArrayList<>();

        // todo set params for individual vs corporation
        try {
            Merchant[] merchantsResponse = restTemplate.getForObject("https://demo.methodfi.com/merchants", Merchant[].class);
            if (merchantsResponse != null) {
                entityList = Arrays.asList(merchantsResponse);
            }
        } catch (Exception e) {
            log.error("oops lmao, retrieve all entities failed");
            log.error(e.getMessage());
        }

        return entityList;
    }
}
