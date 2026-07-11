package com.example.luxury.dominios.finanzas.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class MonedaForm {

	@NotBlank
	@Size(min = 3, max = 3)
	private String codigo;

	@NotBlank
	private String nombre;

	public MonedaRequest toRequest() {
		return new MonedaRequest(codigo, nombre);
	}
}
