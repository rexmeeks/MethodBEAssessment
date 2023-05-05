package com.method.donuts.bos.base;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreuploadPayment {
    private String dunkinBranchId;
    private Integer paymentTotalsInCents;

    public PreuploadPayment(String dunkinBranchId, Integer paymentTotalsInCents) {
        this.dunkinBranchId = dunkinBranchId;
        this.paymentTotalsInCents = paymentTotalsInCents;
    }

    public PreuploadPayment() {

    }
}
