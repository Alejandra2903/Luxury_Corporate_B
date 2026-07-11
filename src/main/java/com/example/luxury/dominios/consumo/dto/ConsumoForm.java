package com.example.luxury.dominios.consumo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

@Data
public class ConsumoForm {

	@NotNull
	private Long sedeId;

	@NotNull
	private Long tipoRecursoId;

	@NotNull
	@Positive
	private BigDecimal cantidadConsumida;

	@NotNull
	@PastOrPresent(message = "La fecha de consumo no puede ser en el futuro")
	private LocalDate fechaConsumo = LocalDate.now();

	@NotBlank
	@Pattern(regexp = "\\d{4}-\\d{2}", message = "El periodo debe tener formato yyyy-MM")
	private String periodo;

	public ConsumoRequest toRequest() {
		return new ConsumoRequest(sedeId, tipoRecursoId, cantidadConsumida, fechaConsumo, periodo);
	}
}
