package com.example.luxury.dominios.tarifa.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.luxury.dominios.common.enums.EstadoRegistro;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.FutureOrPresent;

@Data
public class TarifaForm {

	@NotNull
	private Long sedeId;

	@NotNull
	private Long tipoRecursoId;

	@NotNull
	@Positive
	private BigDecimal precioUnitarioPen;

	@NotNull
	@FutureOrPresent(message = "La fecha no puede estar en el pasado")
	private LocalDate fechaInicio = LocalDate.now();

	@FutureOrPresent(message = "La fecha no puede estar en el pasado")
	private LocalDate fechaFin;

	private EstadoRegistro estado = EstadoRegistro.ACTIVO;

	public TarifaRequest toRequest() {
		return new TarifaRequest(sedeId, tipoRecursoId, precioUnitarioPen, fechaInicio, fechaFin, estado);
	}
}
