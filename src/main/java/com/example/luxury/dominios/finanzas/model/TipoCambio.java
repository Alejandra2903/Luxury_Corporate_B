package com.example.luxury.dominios.finanzas.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.luxury.dominios.common.enums.EstadoRegistro;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "tipos_cambio")
public class TipoCambio {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_tipo_cambio")
	@EqualsAndHashCode.Include
	@ToString.Include
	private Long id;

	@ManyToOne
	@JoinColumn(name = "id_moneda_origen", nullable = false)
	private Moneda monedaOrigen;

	@ManyToOne
	@JoinColumn(name = "id_moneda_destino", nullable = false)
	private Moneda monedaDestino;

	@Column(nullable = false, precision = 14, scale = 6)
	private BigDecimal valor;

	@Column(nullable = false)
	private LocalDate fecha;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EstadoRegistro estado = EstadoRegistro.ACTIVO;
}
