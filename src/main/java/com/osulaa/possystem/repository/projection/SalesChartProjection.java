package com.osulaa.possystem.repository.projection;

import java.time.LocalDate;

public interface SalesChartProjection {

    LocalDate getDate();

    Double getTotal();
}
