package com.method.donuts.bos.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PreuploadResponseBO {
    private List<PreuploadPayment> preuploadPayments;
    private String reportId;
    private String response;

    public PreuploadResponseBO(String response) {
        this.response = response;
    }

    public PreuploadResponseBO () {

    }
}
