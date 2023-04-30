package com.method.donuts.service;


import com.method.donuts.bos.base.ResponseDO;
import com.method.donuts.bos.method.base.MethodObjects;
import com.method.donuts.bos.method.entities.Entity;
import com.method.donuts.bos.report.xml.PayInfoBO;
import com.method.donuts.service.method.MethodEntityService;
import com.method.donuts.transformer.FileTransformer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
public class FileService {

    @Autowired
    private FileTransformer fileTransformer;

    @Autowired
    private MethodEntityService methodEntityService;

    public ResponseDO digestPayInfo(PayInfoBO payInfoBO) {
        try {

            MethodObjects methodObjects = fileTransformer.xmlToMethodObjects(payInfoBO);
            Map<String, String> storesMap = new HashMap<>();

            methodObjects.getStores().forEach((key, value) -> {
                String entityId = methodEntityService.createEntity(value);
                if(entityId != null) {
                    storesMap.put(key, entityId);
                }
            });

            log.info("test");

        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
