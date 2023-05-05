package com.method.donuts.bos.method.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.method.donuts.bos.method.accounts.Account;
import com.method.donuts.bos.method.base.Metadata;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Entity {
    private String id;
    private String type;
    private Individual individual;
    private Corporation corporation;
    private Object receive_only;
    private Address address;
    private ArrayList<String> capabilities;
    private ArrayList<String> available_capabilities;
    private ArrayList<String> pending_capabilities;
    private Object error;
    private String status;
    private Metadata metadata;
    private Date created_at;
    private Date updated_at;

    @JsonIgnore
    private Account achAccount;

    @JsonIgnore
    private Map<String, Account> liabilities = new HashMap<>();

    @JsonIgnore
    private Integer paymentTotal;
}
