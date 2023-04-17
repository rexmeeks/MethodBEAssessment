package com.method.donuts.bos.method.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.method.donuts.bos.method.accounts.Account;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.ArrayList;

@Getter
@Setter
public class Entity {
    public String id;
    public String type;
    public Individual individual;
    public Corporation corporation;
    public Object receive_only;
    public Address address;
    public ArrayList<String> capabilities;
    public ArrayList<String> available_capabilities;
    public ArrayList<String> pending_capabilities;
    public Object error;
    public String status;
    public Object metadata;
    public Date created_at;
    public Date updated_at;

    @JsonIgnore
    public Account achAccount;

    @JsonIgnore
    public Account liability;
}
