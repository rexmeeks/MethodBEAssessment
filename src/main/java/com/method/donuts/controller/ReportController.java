package com.method.donuts.controller;

import com.method.donuts.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.FileInputStream;


@Controller
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/reports/{id}")
    public ResponseEntity<?> getReportByIdAndType(@PathVariable String id, @RequestParam("type") String type) {
        if(id.isEmpty()) {
            return new ResponseEntity<>("Please provide a report id", HttpStatus.BAD_REQUEST);
        }

        try {
            File file = null;
            if(type.equals("method")) {
                file = reportService.getPaymentsByReportSourceAccount(id);
            } else if(type.equals("dunkin")) {
                file = reportService.getPaymentsByReportDunkinBranch(id);
            }
            log.info("test");

            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName() + ".csv");
            // defining the custom Content-Type
            headers.set(HttpHeaders.CONTENT_TYPE, "text/csv");

            return ResponseEntity.ok()
                    .contentLength(file.length())
                    .headers(headers)
                    .body(resource);

        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
