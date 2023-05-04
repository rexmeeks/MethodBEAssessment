package com.method.donuts.controller;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.method.donuts.bos.report.xml.PayInfoBO;
import com.method.donuts.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;


@Controller
@Slf4j
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/uploadPayouts")
    public ResponseEntity<String> uploadPaylist(@RequestParam("file") MultipartFile file) {
        if(file.isEmpty()) {
            return new ResponseEntity<>("should've uploaded a file, kid", HttpStatus.BAD_REQUEST);
        }

        if(file.getOriginalFilename() == null) {
            return new ResponseEntity<>("i think files without names are bad news, probably", HttpStatus.BAD_REQUEST);
        }

        try {
            File path = new File(file.getOriginalFilename());
            path.createNewFile();
            FileOutputStream output = new FileOutputStream(path);
            output.write(file.getBytes());
            output.close();

            XmlMapper xmlMapper = new XmlMapper();
            PayInfoBO payInfoBO = xmlMapper.readValue(path, PayInfoBO.class);
            fileService.digestPayInfo(payInfoBO, path.getName());

            log.info("test");

        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/payoutReports")
    public ResponseEntity getPayoutCSV() {
        return new ResponseEntity(HttpStatus.OK);
    }
}
