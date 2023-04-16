package com.method.donuts.bos.method.accounts;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ach {
    public String routing;
    public String number;
    public String type;
}
