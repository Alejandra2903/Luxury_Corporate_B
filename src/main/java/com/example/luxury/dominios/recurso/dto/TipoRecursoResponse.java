package com.example.luxury.dominios.recurso.dto;

import com.example.luxury.dominios.recurso.model.TipoRecurso;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoRecursoResponse {

	private Long id;
	private String nombre;
	private String unidadMedida;

	public static TipoRecursoResponse from(TipoRecurso tipoRecurso) {
		return new TipoRecursoResponse(tipoRecurso.getId(), tipoRecurso.getNombre(), tipoRecurso.getUnidadMedida());
	}
}
