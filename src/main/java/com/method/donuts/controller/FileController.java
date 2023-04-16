package com.method.donuts.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


@Controller
public class FileController {

    @PostMapping("/uploadPayouts")
    public ResponseEntity uploadPaylist(@RequestParam("file") MultipartFile file) {
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/payoutReports")
    public ResponseEntity getPayoutCSV() {
        return new ResponseEntity(HttpStatus.OK);
    }
}
