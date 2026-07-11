package com.example.luxury.dominios.auditoria.dto;

import java.time.LocalDateTime;

import com.example.luxury.dominios.auditoria.model.Auditoria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaResponse {
	private Long id;
	private Long usuarioId;
	private String usuario;
	private String modulo;
	private String accion;
	private String tablaAfectada;
	private Long idRegistroAfectado;
	private String descripcion;
	private LocalDateTime fecha;

	public static AuditoriaResponse from(Auditoria auditoria) {
		return new AuditoriaResponse(
				auditoria.getId(),
				auditoria.getUsuario().getId(),
				auditoria.getUsuario().getCorreo(),
				auditoria.getModulo(),
				auditoria.getAccion(),
				auditoria.getTablaAfectada(),
				auditoria.getIdRegistroAfectado(),
				auditoria.getDescripcion(),
				auditoria.getFecha());
	}
}
