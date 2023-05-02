package com.method.donuts.bos.method.payments;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Payment {
    private String id;
    private Object reversal_id;
    private Object source_trace_id;
    private Object destination_trace_id;
    private String source;
    private String destination;
    private Integer amount;
    private String description;
    private String status;
    private Object error;
    private Object metadata;
    private String estimated_completion_date;
    private String source_settlement_date;
    private String destination_settlement_date;
    private Object fee;
    private Date created_at;
    private Date updated_at;
}
