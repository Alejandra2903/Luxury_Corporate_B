package com.example.luxury.dominios.umbral.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.luxury.dominios.common.enums.EstadoRegistro;
import com.example.luxury.dominios.recurso.model.TipoRecurso;
import com.example.luxury.dominios.sede.model.Sede;

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
@Table(name = "umbrales")
public class Umbral {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_umbral")
	@EqualsAndHashCode.Include
	@ToString.Include
	private Long id;

	@ManyToOne
	@JoinColumn(name = "id_sede", nullable = false)
	private Sede sede;

	@ManyToOne
	@JoinColumn(name = "id_tipo_recurso", nullable = false)
	private TipoRecurso tipoRecurso;

	@Column(name = "limite_consumo", precision = 14, scale = 4)
	private BigDecimal limiteConsumo;

	@Column(name = "limite_presupuesto_pen", precision = 14, scale = 4)
	private BigDecimal limitePresupuestoPen;

	@Column(name = "fecha_inicio", nullable = false)
	private LocalDate fechaInicio;

	@Column(name = "fecha_fin")
	private LocalDate fechaFin;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EstadoRegistro estado = EstadoRegistro.ACTIVO;
}
