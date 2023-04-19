package com.method.donuts.service.method;

import com.method.donuts.bos.method.reports.Report;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class MethodReportService {

    @Autowired
    private RestTemplate restTemplate;

    public Report createReport(Report reportToCreate) {

        Report responseReport = new Report();
        
        try {
            responseReport = restTemplate.postForObject("https://demo.methodfi.com/reports", reportToCreate, Report.class);
        } catch (Exception e) {
            log.error("oops lmao, retrieve Report failed");
            log.error(e.getMessage());
        }

        return responseReport;
    }

    public Report retrieveReport(String id) {
        Report merchant = new Report();

        // todo set id
        try {
            merchant = restTemplate.getForObject("https://demo.methodfi.com/reports/{id}", Report.class);
        } catch (Exception e) {
            log.error("oops lmao, retrieve all entities failed");
            log.error(e.getMessage());
        }

        return merchant;
    }
}
