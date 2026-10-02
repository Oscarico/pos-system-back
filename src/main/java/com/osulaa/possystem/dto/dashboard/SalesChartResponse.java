package com.osulaa.possystem.dto.dashboard;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SalesChartResponse {

    private LocalDate date;

    private Double total;

}
