package com.method.donuts.service;

import com.method.donuts.bos.method.reports.CsvRow;
import com.method.donuts.service.method.MethodReportService;
import com.method.donuts.transformer.CsvTransformer;
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

    public File getPaymentsByReportSourceAccount(String id) {
        List<CsvRow> csvRowList = methodReportService.retrieveReport(id);
        return csvTransformer.transformCsvRowsToSourceAccountPayments(csvRowList);
    }

    public File getPaymentsByReportDunkinBranch(String id) {
        List<CsvRow> csvRowList = methodReportService.retrieveReport(id);
        return csvTransformer.transformCsvRowsToDunkinBranchPayments(csvRowList);
    }
}
