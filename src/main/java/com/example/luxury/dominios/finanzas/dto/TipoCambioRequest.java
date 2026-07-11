package com.example.luxury.dominios.finanzas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.luxury.dominios.common.enums.EstadoRegistro;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoCambioRequest {

	@NotBlank
	private String monedaOrigen;

	@NotBlank
	private String monedaDestino;

	@NotNull
	@Positive
	private BigDecimal valor;

	@NotNull
	private LocalDate fecha;

	private EstadoRegistro estado;
}
