package com.method.donuts.service;


import com.method.donuts.bos.base.ResponseDO;
import com.method.donuts.bos.method.base.MethodObjects;
import com.method.donuts.bos.report.xml.PayInfoBO;
import com.method.donuts.transformer.FileTransformer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class FileService {

    @Autowired
    private FileTransformer fileTransformer;

    public ResponseDO digestPayInfo(PayInfoBO payInfoBO) {
        try {

            MethodObjects methodObjects = fileTransformer.xmlToMethodObjects(payInfoBO);

            log.info("test");

        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
