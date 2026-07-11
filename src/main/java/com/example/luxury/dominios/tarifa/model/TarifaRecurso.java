package com.example.luxury.dominios.tarifa.model;

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
@Table(name = "tarifas_recurso")
public class TarifaRecurso {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_tarifa")
	@EqualsAndHashCode.Include
	@ToString.Include
	private Long id;

	@ManyToOne
	@JoinColumn(name = "id_sede", nullable = false)
	private Sede sede;

	@ManyToOne
	@JoinColumn(name = "id_tipo_recurso", nullable = false)
	private TipoRecurso tipoRecurso;

	@Column(name = "precio_unitario_pen", nullable = false, precision = 14, scale = 4)
	private BigDecimal precioUnitarioPen;

	@Column(name = "fecha_inicio", nullable = false)
	private LocalDate fechaInicio;

	@Column(name = "fecha_fin")
	private LocalDate fechaFin;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EstadoRegistro estado = EstadoRegistro.ACTIVO;
}
