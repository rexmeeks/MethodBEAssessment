package com.method.donuts.bos.report.xml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RowBO {

    @JsonProperty("Employee")
    private EmployeeBO employee;

    @JsonProperty("Payor")
    private PayorBO payor;

    @JsonProperty("Payee")
    private PayeeBO payeeBO;

    @JsonProperty("Amount")
    private String amount;
}
