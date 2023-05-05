package com.method.donuts.bos.base;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDO {
    private String dunkinBranchId;
    private Integer totalPaymentsInCents;
}
