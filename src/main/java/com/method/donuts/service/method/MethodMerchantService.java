package com.method.donuts.service.method;

import com.method.donuts.bos.method.merchants.Merchant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


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
    
}
