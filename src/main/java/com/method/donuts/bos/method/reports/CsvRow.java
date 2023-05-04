package com.method.donuts.bos.method.reports;

import com.method.donuts.bos.method.base.Metadata;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvRecurse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CsvRow {
    @CsvBindByName
    private String payment_id;

    @CsvBindByName
    private String payment_fund_status;

    @CsvBindByName
    private String payment_status;

    @CsvBindByName
    private Integer payment_amount;

    @CsvBindByName
    @CsvDate(value = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime payment_created_at;

    @CsvBindByName
    @CsvDate(value = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime payment_updated_at;

    @CsvBindByName
    private String payment_source_account_id;

    @CsvBindByName
    private String payment_source_holder_id;

    @CsvBindByName
    private String payment_destination_holder_id;

    @CsvBindByName
    private String payment_destination_account_id;

    @CsvBindByName
    private String payment_metadata;
}
