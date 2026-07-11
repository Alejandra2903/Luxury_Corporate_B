package com.example.luxury.dominios.recurso.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class TipoRecursoForm {

	@NotBlank
	private String nombre;

	@NotBlank
	private String unidadMedida;

	public TipoRecursoRequest toRequest() {
		return new TipoRecursoRequest(nombre, unidadMedida);
	}
}
