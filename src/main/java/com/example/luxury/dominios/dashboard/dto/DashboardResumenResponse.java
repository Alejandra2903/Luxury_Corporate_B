package com.example.luxury.dominios.dashboard.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResumenResponse {
	private long totalSedes;
	private long totalConsumos;
	private long totalAlertas;
	private BigDecimal costoTotalPen;
	private BigDecimal costoTotalUsd;
	private BigDecimal costoTotalEur;
}
