package com.method.donuts.bos.method.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Corporation {

    public String name;
    public String dba;
    public String ein;
    public ArrayList<Owner> owners;

}
