package com.method.donuts.bos.method.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Owner {
    public String first_name;
    public String last_name;
    public String phone;
    public String email;
    public String dob;
    public Address address;
}
