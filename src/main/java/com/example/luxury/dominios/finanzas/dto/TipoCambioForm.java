package com.example.luxury.dominios.finanzas.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.luxury.dominios.common.enums.EstadoRegistro;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
public class TipoCambioForm {

	@NotBlank
	private String monedaOrigen = "PEN";

	@NotBlank
	private String monedaDestino;

	@NotNull
	@Positive
	private BigDecimal valor;

	@NotNull
	private LocalDate fecha = LocalDate.now();

	private EstadoRegistro estado = EstadoRegistro.ACTIVO;

	public TipoCambioRequest toRequest() {
		return new TipoCambioRequest(monedaOrigen, monedaDestino, valor, fecha, estado);
	}
}
