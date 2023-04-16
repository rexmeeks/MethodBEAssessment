package com.method.donuts.bos.report.xml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayorBO {

    @JsonProperty("DunkinId")
    private String dunkinId;

    @JsonProperty("DunkinId")
    private Integer ABARouting;

    @JsonProperty("AccountNumber")
    private Integer accountNumber;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("DBA")
    private String dba;

    @JsonProperty("EIN")
    private Integer ein;

    @JsonProperty("Address")
    private AddressBO address;
}
