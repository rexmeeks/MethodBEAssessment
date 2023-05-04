package com.method.donuts.service.method;

import com.method.donuts.bos.method.reports.CsvRow;
import com.method.donuts.bos.method.reports.Report;
import com.method.donuts.bos.method.reports.ReportData;
import com.method.donuts.transformer.CsvTransformer;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class MethodReportService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CsvTransformer csvTransformer;

    @Value("${api-key}")
    private String apiKey;

    @Autowired
    private Bucket bucket;

    public String createReport() {

        Report reportToCreate = new Report();
        reportToCreate.setType("payments.created.current");
        ReportData reportData;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Report> request = new HttpEntity<>(reportToCreate, headers);

        try {
            bucket.asBlocking().consume(1);
            reportData = restTemplate.postForObject("https://dev.methodfi.com/reports", request, ReportData.class);
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

        if(reportData != null && !reportData.getData().isEmpty() && reportData.getData().get(0) != null) {
            return reportData.getData().get(0).getId();
        }

        return null;
    }

    public ReportData retrieveReport(String id) {
        ReportData reportData = null;

//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + apiKey);
//        headers.setContentType(MediaType.APPLICATION_JSON);

//        HttpEntity<Report> request = new HttpEntity<>(null, headers);
        RequestCallback requestCallback = request -> {
            request
                    .getHeaders()
                    .setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
            request.getHeaders().set("Authorization", "Bearer " + apiKey);
        };


        // todo set id
        try {
            bucket.asBlocking().consume(1);
            File file = restTemplate.execute("https://dev.methodfi.com/reports/" + id + "/download", HttpMethod.GET, requestCallback, clientHttpResponse -> {
                File ret = File.createTempFile("download", "tmp");
                StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(ret));
                return ret;
            });
//            ResponseEntity<ReportData> responseEntity = restTemplate.exchange(, HttpMethod.GET, request, ReportData.class);
//            reportData = responseEntity.getBody();
            Reader reader = new FileReader(file);
            CsvToBean<CsvRow> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(CsvRow.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            List<CsvRow> listOfPayments = csvToBean.parse();
            File file1 = csvTransformer.transformCsvRowsToDunkinBranchPayments(listOfPayments);
            log.info("test");

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

        return reportData;
    }
}
