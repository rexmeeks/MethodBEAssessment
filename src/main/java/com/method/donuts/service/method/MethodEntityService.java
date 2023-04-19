package com.method.donuts.service.method;

import com.method.donuts.bos.method.entities.Entity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
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

    public Entity createEntity(Entity entityToCreate) {

        Entity responseEntity = new Entity();

        try {
            responseEntity = restTemplate.postForObject("https://demo.methodfi.com/entities", entityToCreate, Entity.class);
        } catch (Exception e) {
            log.error("oops lmao, retrieve entity failed");
            log.error(e.getMessage());
        }

        return responseEntity;
    }

    public List<Entity> retrieveAllEntities(String type) {
        List<Entity> entityList = new ArrayList<>();

        // todo set params for individual vs corporation
        try {
            Entity[] entitiesResponse = restTemplate.getForObject("https://demo.methodfi.com/entities", Entity[].class);
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
