package com.method.donuts.bos.report.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayeeBO {

    @JsonProperty("PlaidId")
    private String plaidId;

    @JsonProperty("LoanAccountNumber")
    private String loanAccountNumber;
}
