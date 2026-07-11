package com.example.luxury.dominios.consumo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.luxury.dominios.consumo.model.Consumo;
import com.example.luxury.dominios.finanzas.model.ConsumoCosto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsumoResponse {
	private Long id;
	private Long sedeId;
	private String sede;
	private Long tipoRecursoId;
	private String tipoRecurso;
	private String unidadMedida;
	private BigDecimal cantidadConsumida;
	private LocalDate fechaConsumo;
	private String periodo;
	private LocalDateTime creadoEn;
	private List<CostoResponse> costos;

	public static ConsumoResponse from(Consumo consumo, List<ConsumoCosto> costos) {
		return new ConsumoResponse(
				consumo.getId(),
				consumo.getSede().getId(),
				consumo.getSede().getNombre(),
				consumo.getTipoRecurso().getId(),
				consumo.getTipoRecurso().getNombre(),
				consumo.getTipoRecurso().getUnidadMedida(),
				consumo.getCantidadConsumida(),
				consumo.getFechaConsumo(),
				consumo.getPeriodo(),
				consumo.getCreadoEn(),
				costos.stream().map(CostoResponse::from).toList());
	}
}
