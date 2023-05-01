package com.method.donuts.service.method;

import com.method.donuts.bos.method.entities.EntityData;
import com.method.donuts.bos.method.entities.Entity;
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
public class MethodEntityService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${api-key}")
    private String apiKey;

    @Autowired
    private Bucket bucket;

    public String createEntity(Entity entityToCreate) {

        EntityData entityData;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Entity> request = new HttpEntity<>(entityToCreate, headers);

        try {
            bucket.asBlocking().consume(1);
            entityData = restTemplate.postForObject("https://dev.methodfi.com/entities", request, EntityData.class);
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

        if(entityData != null && !entityData.getData().isEmpty() && entityData.getData().get(0) != null) {
            return entityData.getData().get(0).getId();
        }

        return null;
    }

    public List<Entity> retrieveAllEntities(String type) {
        List<Entity> entityList = new ArrayList<>();

        // todo set params for individual vs corporation
        try {
            Entity[] entitiesResponse = restTemplate.getForObject("https://dev.methodfi.com/entities", Entity[].class);
            if (entitiesResponse != null) {
               entityList = Arrays.asList(entitiesResponse);
            }
        } catch (Exception e) {
            log.error("oops lmao, retrieve all entities failed");
            log.error(e.getMessage());
        }

        return entityList;
    }
}
