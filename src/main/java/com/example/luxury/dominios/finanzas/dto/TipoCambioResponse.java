package com.example.luxury.dominios.finanzas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.luxury.dominios.common.enums.EstadoRegistro;
import com.example.luxury.dominios.finanzas.model.TipoCambio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoCambioResponse {

	private Long id;
	private String monedaOrigen;
	private String monedaDestino;
	private BigDecimal valor;
	private LocalDate fecha;
	private EstadoRegistro estado;

	public static TipoCambioResponse from(TipoCambio tipoCambio) {
		return new TipoCambioResponse(
				tipoCambio.getId(),
				tipoCambio.getMonedaOrigen().getCodigo(),
				tipoCambio.getMonedaDestino().getCodigo(),
				tipoCambio.getValor(),
				tipoCambio.getFecha(),
				tipoCambio.getEstado());
	}
}
