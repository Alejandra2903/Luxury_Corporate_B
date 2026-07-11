package com.example.luxury.dominios.recurso.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoRecursoRequest {

	@NotBlank
	private String nombre;

	@NotBlank
	private String unidadMedida;
}
