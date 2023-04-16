package com.method.donuts.bos.report.xml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class EmployeeBO {

    @JsonProperty("DunkinId")
    private String dunkinId;

    @JsonProperty("DunkinBranch")
    private String dunkinBranch;

    @JsonProperty("FirstName")
    private String firstName;

    @JsonProperty("LastName")
    private String lastName;

    @JsonProperty("DOB")
    private Date dateOfBirth;

    @JsonProperty("PhoneNumber")
    private String phoneNumber;
}
