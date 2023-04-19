package com.method.donuts.bos.report.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RowBO {

    @JsonProperty("Employee")
    private EmployeeBO employee;

    @JsonProperty("Payor")
    private PayorBO payor;

    @JsonProperty("Payee")
    private PayeeBO payee;

    @JsonProperty("Amount")
    private String amount;
}
