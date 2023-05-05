package com.method.donuts.service;

import com.method.donuts.bos.base.ReportsResponse;
import com.method.donuts.bos.method.reports.CsvRow;
import com.method.donuts.entities.Reports;
import com.method.donuts.repository.ReportsRepository;
import com.method.donuts.service.method.MethodReportService;
import com.method.donuts.transformer.CsvTransformer;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private MethodReportService methodReportService;

    @Autowired
    private CsvTransformer csvTransformer;

    @Autowired
    private ReportsRepository reportsRepository;

    public File getPaymentsByReportSourceAccount(String id) {
        List<CsvRow> csvRowList = methodReportService.retrieveReport(id);
        return csvTransformer.transformCsvRowsToSourceAccountPayments(csvRowList);
    }

    public File getPaymentsByReportDunkinBranch(String id) {
        List<CsvRow> csvRowList = methodReportService.retrieveReport(id);
        return csvTransformer.transformCsvRowsToDunkinBranchPayments(csvRowList);
    }

    public ReportsResponse getAllReports() {
        ReportsResponse reportsResponse = new ReportsResponse();
        reportsResponse.setReportsList(IterableUtils.toList(reportsRepository.findAll()));
        return reportsResponse;
    }
}
