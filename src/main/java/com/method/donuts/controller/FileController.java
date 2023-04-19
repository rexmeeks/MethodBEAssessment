package com.method.donuts.controller;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.method.donuts.bos.report.xml.PayInfoBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Controller
@Slf4j
public class FileController {

    @PostMapping("/uploadPayouts")
    public ResponseEntity uploadPaylist(@RequestParam("file") MultipartFile file) {
        try {

            File path = new File(file.getOriginalFilename());
            path.createNewFile();
            FileOutputStream output = new FileOutputStream(path);
            output.write(file.getBytes());
            output.close();

            XmlMapper xmlMapper = new XmlMapper();
            PayInfoBO payInfoBO = xmlMapper.readValue(path, PayInfoBO.class);

            log.info("test");

        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/payoutReports")
    public ResponseEntity getPayoutCSV() {
        return new ResponseEntity(HttpStatus.OK);
    }
}
