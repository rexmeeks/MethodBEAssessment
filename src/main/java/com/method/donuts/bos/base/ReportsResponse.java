package com.method.donuts.bos.base;

import com.method.donuts.entities.Reports;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReportsResponse {
    private List<Reports> reportsList;
}
