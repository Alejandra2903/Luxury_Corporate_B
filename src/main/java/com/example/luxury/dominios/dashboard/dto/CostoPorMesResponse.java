package com.example.luxury.dominios.dashboard.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CostoPorMesResponse {
	private String periodo;
	private String moneda;
	private BigDecimal total;
}
