package com.method.donuts.bos.method.merchants;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class Merchant {
    public String mch_id;
    public String parent_name;
    public String name;
    public String logo;
    public String description;
    public String note;
    public ArrayList<String> types;
    public ArrayList<String> account_prefixes;
    public ProviderIds provider_ids;
}
