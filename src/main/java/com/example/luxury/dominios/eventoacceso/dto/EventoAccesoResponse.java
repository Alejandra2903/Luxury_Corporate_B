package com.example.luxury.dominios.eventoacceso.dto;

import java.time.LocalDateTime;

import com.example.luxury.dominios.common.enums.TipoEventoAcceso;
import com.example.luxury.dominios.eventoacceso.model.EventoAcceso;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventoAccesoResponse {
	private Long id;
	private Long usuarioId;
	private String emailIntentado;
	private TipoEventoAcceso tipoEvento;
	private String descripcion;
	private LocalDateTime fecha;
	private String ip;

	public static EventoAccesoResponse from(EventoAcceso evento) {
		Long usuarioId = evento.getUsuario() == null ? null : evento.getUsuario().getId();
		return new EventoAccesoResponse(
				evento.getId(),
				usuarioId,
				evento.getEmailIntentado(),
				evento.getTipoEvento(),
				evento.getDescripcion(),
				evento.getFecha(),
				evento.getIp());
	}
}
