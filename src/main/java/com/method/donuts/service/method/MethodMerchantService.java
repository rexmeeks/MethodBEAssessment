package com.method.donuts.service.method;

import com.method.donuts.bos.method.entities.Entity;
import com.method.donuts.bos.method.merchants.MerchantData;
import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;


@Service
@Slf4j
public class MethodMerchantService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${api-key}")
    private String apiKey;

    @Autowired
    private Bucket bucket;

    public String retrieveMerchant(String id) {

        MerchantData merchantData;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        UriComponents builder = UriComponentsBuilder.fromHttpUrl("https://dev.methodfi.com/merchants/")
                .queryParam("provider_id.plaid",id)
                .build();

        HttpEntity<Entity> request = new HttpEntity<>(null, headers);

        try {
            bucket.asBlocking().consume(1);
            ResponseEntity<MerchantData> responseEntity = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, request, MerchantData.class);
            merchantData = responseEntity.getBody();
        } catch(InterruptedException e) {
            log.error("bucket blocking was interuppted, but probably shouldn't happen");
            log.error(e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("oops lmao, retrieve entity failed");
            log.error(e.getMessage());
            return null;
        }

        if(merchantData != null && !merchantData.getData().isEmpty() && merchantData.getData().get(0) != null) {
            return merchantData.getData().get(0).getMch_id();
        }

        return null;
    }
}
