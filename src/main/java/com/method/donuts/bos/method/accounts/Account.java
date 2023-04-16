package com.method.donuts.bos.method.accounts;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.ArrayList;

@Getter
@Setter
public class Account {
    public String id;
    public String holder_id;
    public String type;
    public Ach ach;
    public Liability liability;
    public String status;
    public ArrayList<String> capabilities;
    public ArrayList<Object> available_capabilities;
    public Object error;
    public Date created_at;
    public Date updated_at;
}
