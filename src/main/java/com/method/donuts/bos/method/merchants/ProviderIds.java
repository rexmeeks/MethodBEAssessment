package com.method.donuts.bos.method.merchants;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class ProviderIds {
    public ArrayList<String> plaid;
    public ArrayList<String> mx;
    public ArrayList<String> finicity;
}
