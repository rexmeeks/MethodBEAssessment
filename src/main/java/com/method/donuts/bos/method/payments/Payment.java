package com.method.donuts.bos.method.payments;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class Payment {
    public String id;
    public Object reversal_id;
    public Object source_trace_id;
    public Object destination_trace_id;
    public String source;
    public String destination;
    public Float amount;
    public String description;
    public String status;
    public Object error;
    public Object metadata;
    public String estimated_completion_date;
    public String source_settlement_date;
    public String destination_settlement_date;
    public Object fee;
    public Date created_at;
    public Date updated_at;
}
