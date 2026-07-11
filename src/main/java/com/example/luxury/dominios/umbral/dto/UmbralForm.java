package com.example.luxury.dominios.umbral.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.luxury.dominios.common.enums.EstadoRegistro;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.FutureOrPresent;

@Data
public class UmbralForm {

	@NotNull
	private Long sedeId;

	@NotNull
	private Long tipoRecursoId;

	@PositiveOrZero
	private BigDecimal limiteConsumo;

	@PositiveOrZero
	private BigDecimal limitePresupuestoPen;

	@NotNull
	@FutureOrPresent(message = "La fecha no puede estar en el pasado")
	private LocalDate fechaInicio = LocalDate.now();

	@FutureOrPresent(message = "La fecha no puede estar en el pasado")
	private LocalDate fechaFin;

	private EstadoRegistro estado = EstadoRegistro.ACTIVO;

	public UmbralRequest toRequest() {
		return new UmbralRequest(sedeId, tipoRecursoId, limiteConsumo, limitePresupuestoPen, fechaInicio, fechaFin, estado);
	}
}
