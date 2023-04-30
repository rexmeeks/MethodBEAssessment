package com.method.donuts.service.method;

import com.method.donuts.bos.method.base.BaseResponse;
import com.method.donuts.bos.method.entities.DataEntity;
import com.method.donuts.bos.method.entities.Entity;
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

    public String createEntity(Entity entityToCreate) {

        DataEntity dataEntity;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Entity> request = new HttpEntity<>(entityToCreate, headers);

        try {
            dataEntity = restTemplate.postForObject("https://dev.methodfi.com/entities", request, DataEntity.class);
        } catch (Exception e) {
            log.error("oops lmao, retrieve entity failed");
            log.error(e.getMessage());
            return null;
        }

        if(dataEntity != null && !dataEntity.getData().isEmpty() && dataEntity.getData().get(0) != null) {
            return dataEntity.getData().get(0).getId();
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
