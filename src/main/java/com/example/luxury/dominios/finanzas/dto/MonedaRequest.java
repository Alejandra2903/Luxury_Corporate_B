package com.example.luxury.dominios.finanzas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonedaRequest {

	@NotBlank
	@Size(min = 3, max = 3)
	private String codigo;

	@NotBlank
	private String nombre;
}
