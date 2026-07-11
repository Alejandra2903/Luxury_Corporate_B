package com.example.luxury.dominios.sede.dto;

import com.example.luxury.dominios.common.enums.EstadoRegistro;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SedeRequest {

	@NotBlank
	private String nombre;

	@NotBlank
	private String ciudad;

	@NotBlank
	private String direccion;

	private EstadoRegistro estado;
}
