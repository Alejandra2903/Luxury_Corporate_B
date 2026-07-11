package com.example.luxury.dominios.consumo.dto;

import java.math.BigDecimal;

import com.example.luxury.dominios.finanzas.model.ConsumoCosto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CostoResponse {
	private String moneda;
	private BigDecimal monto;
	private BigDecimal tipoCambioUsado;

	public static CostoResponse from(ConsumoCosto costo) {
		BigDecimal tipoCambio = costo.getTipoCambio() == null ? null : costo.getTipoCambio().getValor();
		return new CostoResponse(costo.getMoneda().getCodigo(), costo.getMontoCalculado(), tipoCambio);
	}
}
