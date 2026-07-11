package com.example.luxury.dominios.tarifa.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.luxury.dominios.common.enums.EstadoRegistro;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarifaRequest {
	@NotNull
	private Long sedeId;

	@NotNull
	private Long tipoRecursoId;

	@NotNull
	@Positive
	private BigDecimal precioUnitarioPen;

	@NotNull
	private LocalDate fechaInicio;

	private LocalDate fechaFin;
	private EstadoRegistro estado;
}
