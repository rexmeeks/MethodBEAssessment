package com.method.donuts;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class DonutsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DonutsApplication.class, args);
    }

    @Bean
    public RestTemplate getRestTemplate() {
//        return new RestTemplate();
        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new LoggingRequestInterceptor());
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }

    @Bean
    public Bucket getBucket() {
        Bandwidth limit = Bandwidth.simple(600, Duration.ofMinutes(1));
        // construct the bucket
        return Bucket.builder().addLimit(limit).build();
    }
}
