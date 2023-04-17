package com.method.donuts.bos.method.accounts;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class Liability {
    public String mch_id;
    public String mask;
    public String type;
    public String payment_status;
    public String data_status;
    public Date data_last_successful_sync;
    public Object data_status_error;
    public String data_source;
    public Date data_updated_at;
    public String ownership;
    public String number; // is this irony?
    public StudentLoan student_loan;
}
