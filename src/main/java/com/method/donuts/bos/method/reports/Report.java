package com.method.donuts.bos.method.reports;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Report {
    private String id;
    private String type;
    private String url;
    private String status;
    private Object metadata;
    private Date created_at;
    private Date updated_at;
}
