package com.example.luxury.dominios.alerta.dto;

import java.time.LocalDateTime;

import com.example.luxury.dominios.alerta.model.Alerta;
import com.example.luxury.dominios.common.enums.EstadoAlerta;
import com.example.luxury.dominios.common.enums.NivelAlerta;
import com.example.luxury.dominios.common.enums.TipoAlerta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertaResponse {
	private Long id;
	private Long consumoId;
	private String sede;
	private String tipoRecurso;
	private TipoAlerta tipoAlerta;
	private String mensaje;
	private NivelAlerta nivel;
	private EstadoAlerta estado;
	private LocalDateTime fechaGeneracion;

	public static AlertaResponse from(Alerta alerta) {
		Long consumoId = alerta.getConsumo() != null ? alerta.getConsumo().getId() : null;
		String sede = alerta.getConsumo() != null && alerta.getConsumo().getSede() != null ? alerta.getConsumo().getSede().getNombre() : "Alerta Manual";
		String tipoRecurso = alerta.getConsumo() != null && alerta.getConsumo().getTipoRecurso() != null ? alerta.getConsumo().getTipoRecurso().getNombre() : "General";

		return new AlertaResponse(
				alerta.getId(),
				consumoId,
				sede,
				tipoRecurso,
				alerta.getTipoAlerta(),
				alerta.getMensaje(),
				alerta.getNivel(),
				alerta.getEstado(),
				alerta.getFechaGeneracion());
	}
}
