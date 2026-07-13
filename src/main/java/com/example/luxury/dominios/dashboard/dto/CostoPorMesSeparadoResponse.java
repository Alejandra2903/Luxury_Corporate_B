package com.example.luxury.dominios.dashboard.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CostoPorMesSeparadoResponse {
	private String periodo;
	private BigDecimal costoEnergia;
	private BigDecimal costoAgua;
	private BigDecimal costoTotal;
}
