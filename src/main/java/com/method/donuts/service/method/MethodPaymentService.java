package com.method.donuts.service.method;

import com.method.donuts.bos.method.payments.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class MethodPaymentService {

    @Autowired
    private RestTemplate restTemplate;

    public Payment createPayment(Payment paymentToCreate) {

        Payment responsePayment = new Payment();

        try {
            responsePayment = restTemplate.postForObject("https://demo.methodfi.com/accounts", paymentToCreate, Payment.class);
        } catch (Exception e) {
            log.error("oops lmao, retrieve Payment failed");
            log.error(e.getMessage());
        }

        return responsePayment;
    }
    
}
