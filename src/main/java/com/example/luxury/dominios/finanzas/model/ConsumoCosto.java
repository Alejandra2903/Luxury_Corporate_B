package com.example.luxury.dominios.finanzas.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.luxury.dominios.consumo.model.Consumo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "consumo_costos")
public class ConsumoCosto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_consumo_costo")
	@EqualsAndHashCode.Include
	@ToString.Include
	private Long id;

	@ManyToOne
	@JoinColumn(name = "id_consumo", nullable = false)
	private Consumo consumo;

	@ManyToOne
	@JoinColumn(name = "id_moneda", nullable = false)
	private Moneda moneda;

	@ManyToOne
	@JoinColumn(name = "id_tipo_cambio")
	private TipoCambio tipoCambio;

	@Column(name = "monto_calculado", nullable = false, precision = 14, scale = 4)
	private BigDecimal montoCalculado;

	@Column(name = "fecha_calculo", nullable = false)
	private LocalDateTime fechaCalculo;

	@PrePersist
	void prePersist() {
		if (fechaCalculo == null) {
			fechaCalculo = LocalDateTime.now();
		}
	}
}
