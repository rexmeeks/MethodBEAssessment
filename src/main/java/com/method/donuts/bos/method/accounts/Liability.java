package com.method.donuts.bos.method.accounts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Liability {
    private String mch_id;
    private String mask;
    private String type;
    private String payment_status;
    private String data_status;
    private Date data_last_successful_sync;
    private Object data_status_error;
    private String data_source;
    private Date data_updated_at;
    private String ownership;
    private String number; // is this irony?
    private StudentLoan student_loan;

    @JsonIgnore
    private String plaid_id;
    
}
