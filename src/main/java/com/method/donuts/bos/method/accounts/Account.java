package com.method.donuts.bos.method.accounts;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.method.donuts.bos.method.base.Metadata;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.ArrayList;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Account {
    private String id;
    private String holder_id;
    private String type;
    private Ach ach;
    private Liability liability;
    private String status;
    private ArrayList<String> capabilities;
    private ArrayList<Object> available_capabilities;
    private Object error;
    private Date created_at;
    private Date updated_at;
    private Metadata metadata;
}
