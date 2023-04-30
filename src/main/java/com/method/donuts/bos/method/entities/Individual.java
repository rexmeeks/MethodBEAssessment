package com.method.donuts.bos.method.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Individual {
    public String first_name;
    public String last_name;
    public String phone;
    public String email;
    public String dob;
}
