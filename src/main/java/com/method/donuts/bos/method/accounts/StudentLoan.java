package com.method.donuts.bos.method.accounts;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.ArrayList;

@Getter
@Setter
public class StudentLoan {
    public String name;
    public String sub_type;
    public Double sequence;
    public Double balance;
    public Object original_loan_amount;
    public Double last_payment_amount;
    public String last_payment_date;
    public String next_payment_due_date;
    public Double next_payment_minimum_amount;
    public String disbursed_at;
    public Integer interest_rate_percentage;
    public String interest_rate_type;
    public String interest_rate_source;
    public Object delinquent_status;
    public Object delinquent_amount;
    public Object delinquent_period;
    public Object delinquent_action;
    public Date delinquent_start_date;
    public Date delinquent_major_start_date;
    public Date delinquent_status_updated_at;
    public ArrayList<Object> delinquent_history;
}
