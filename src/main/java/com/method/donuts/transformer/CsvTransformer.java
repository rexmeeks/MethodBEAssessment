package com.method.donuts.transformer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.method.donuts.bos.method.base.Metadata;
import com.method.donuts.bos.method.reports.CsvRow;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class CsvTransformer {

    public File transformCsvRowsToSourceAccountPayments(List<CsvRow> csvRowList) {
        Map<String, Integer> accountPaymentTotals = new HashMap<>();
        csvRowList.stream().forEach(csvRow -> {
            // only get the ones with metadata, (this is because I added metadata after doing a lot of testing)
            if(!csvRow.getPayment_metadata().isEmpty()) {
                if (accountPaymentTotals.containsKey(csvRow.getPayment_source_account_id())) {
                    accountPaymentTotals.replace(csvRow.getPayment_source_account_id(), accountPaymentTotals.get(csvRow.getPayment_source_account_id()) + csvRow.getPayment_amount());
                } else {
                    accountPaymentTotals.put(csvRow.getPayment_source_account_id(), csvRow.getPayment_amount());
                }
            }
        });

        return createSimplePaymentCsvWithColumnNames(accountPaymentTotals, new String[] {"source_account_id", "total_amount_payed_in_cents"});
    }

    public File transformCsvRowsToDunkinBranchPayments(List<CsvRow> csvRowList) {
        Map<String, Integer> accountPaymentTotals = new HashMap<>();
        csvRowList.stream().forEach(csvRow -> {
            // only get the ones with metadata, (this is because I added metadata after doing a lot of testing)
            if(!csvRow.getPayment_metadata().isEmpty()) {
                String branchId = convertPaymentMetadataStringToMetadata(csvRow.getPayment_metadata()).getSourceDunkinId();
                if (accountPaymentTotals.containsKey(branchId)) {
                    accountPaymentTotals.replace(branchId, accountPaymentTotals.get(branchId) + csvRow.getPayment_amount());
                } else {
                    accountPaymentTotals.put(branchId, csvRow.getPayment_amount());
                }
            }
        });

        return createSimplePaymentCsvWithColumnNames(accountPaymentTotals, new String[] {"dunkin_branch_id", "total_amount_payed_in_cents"});
    }

    private File createSimplePaymentCsvWithColumnNames(Map<String, Integer> accountPaymentTotals, String[] columns) {
        // create CSV to return
        try {
            File file = File.createTempFile("download", "tmp");
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile, ',',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            // create a List which contains String array
            List<String[]> data = sourceAccountMapToStringArray(accountPaymentTotals, columns);
            writer.writeAll(data);
            writer.close();
            return file;
        } catch (Exception e) {
            log.error("error in converting to CSV");
            log.error(e.getMessage());
        }
        return null;
    }

    private Metadata convertPaymentMetadataStringToMetadata(String metadataString) {
        Metadata metadata = null;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            metadata = objectMapper.readValue(metadataString, Metadata.class);
        } catch (Exception e) {
            log.error("this isn't good, but shouldn't happen");
        }

        return metadata;
    }

    private List<String[]> sourceAccountMapToStringArray(Map<String, Integer> map, String[] column) {
        List<String[]> stringList = new ArrayList<>();
        stringList.add(column);
        map.forEach((key, value) -> {
            stringList.add(new String[] {key, value.toString()});
        });
        return stringList;
    }
}
