package com.example.luxury.dominios.consumo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsumoRequest {
	@NotNull
	private Long sedeId;

	@NotNull
	private Long tipoRecursoId;

	@NotNull
	@Positive
	private BigDecimal cantidadConsumida;

	@NotNull
	private LocalDate fechaConsumo;

	@NotBlank
	@Pattern(regexp = "\\d{4}-\\d{2}", message = "El periodo debe tener formato yyyy-MM")
	private String periodo;
}
