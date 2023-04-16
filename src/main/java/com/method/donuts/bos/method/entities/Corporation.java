package com.method.donuts.bos.method.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class Corporation {

    public String name;
    public String dba;
    public String ein;
    public ArrayList<Owner> owners;

}
