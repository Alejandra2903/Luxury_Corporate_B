package com.example.luxury.dominios.reporte.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteMensualResponse {
	private String periodo;
	private String sede;
	private String tipoRecurso;
	private BigDecimal totalConsumido;
	private BigDecimal costoPen;
	private BigDecimal costoUsd;
	private BigDecimal costoEur;
}
