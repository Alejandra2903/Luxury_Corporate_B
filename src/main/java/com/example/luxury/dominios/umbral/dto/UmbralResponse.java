package com.example.luxury.dominios.umbral.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.luxury.dominios.common.enums.EstadoRegistro;
import com.example.luxury.dominios.umbral.model.Umbral;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UmbralResponse {
	private Long id;
	private Long sedeId;
	private String sede;
	private Long tipoRecursoId;
	private String tipoRecurso;
	private BigDecimal limiteConsumo;
	private BigDecimal limitePresupuestoPen;
	private LocalDate fechaInicio;
	private LocalDate fechaFin;
	private EstadoRegistro estado;

	public static UmbralResponse from(Umbral umbral) {
		return new UmbralResponse(
				umbral.getId(),
				umbral.getSede().getId(),
				umbral.getSede().getNombre(),
				umbral.getTipoRecurso().getId(),
				umbral.getTipoRecurso().getNombre(),
				umbral.getLimiteConsumo(),
				umbral.getLimitePresupuestoPen(),
				umbral.getFechaInicio(),
				umbral.getFechaFin(),
				umbral.getEstado());
	}
}
