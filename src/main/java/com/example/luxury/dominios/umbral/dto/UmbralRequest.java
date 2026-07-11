package com.example.luxury.dominios.umbral.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.luxury.dominios.common.enums.EstadoRegistro;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UmbralRequest {
	@NotNull
	private Long sedeId;

	@NotNull
	private Long tipoRecursoId;

	@PositiveOrZero
	private BigDecimal limiteConsumo;

	@PositiveOrZero
	private BigDecimal limitePresupuestoPen;

	@NotNull
	private LocalDate fechaInicio;

	private LocalDate fechaFin;
	private EstadoRegistro estado;
}
