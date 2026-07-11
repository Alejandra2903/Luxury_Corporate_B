package com.example.luxury.dominios.sede.dto;

import lombok.Data;

import com.example.luxury.dominios.common.enums.EstadoRegistro;

import jakarta.validation.constraints.NotBlank;

@Data
public class SedeForm {

	@NotBlank
	private String nombre;

	@NotBlank
	private String ciudad;

	@NotBlank
	private String direccion;

	private EstadoRegistro estado = EstadoRegistro.ACTIVO;

	public SedeRequest toRequest() {
		return new SedeRequest(nombre, ciudad, direccion, estado);
	}
}
