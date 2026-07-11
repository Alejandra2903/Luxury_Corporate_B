package com.example.luxury.dominios.consumo.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.luxury.dominios.recurso.model.TipoRecurso;
import com.example.luxury.dominios.sede.model.Sede;
import com.example.luxury.dominios.tarifa.model.TarifaRecurso;
import com.example.luxury.dominios.seguridad.models.Usuario;

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
@Table(name = "consumos")
public class Consumo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_consumo")
	@EqualsAndHashCode.Include
	@ToString.Include
	private Long id;

	@ManyToOne
	@JoinColumn(name = "id_sede", nullable = false)
	private Sede sede;

	@ManyToOne
	@JoinColumn(name = "id_tipo_recurso", nullable = false)
	private TipoRecurso tipoRecurso;

	@ManyToOne
	@JoinColumn(name = "id_tarifa", nullable = false)
	private TarifaRecurso tarifa;

	@ManyToOne
	@JoinColumn(name = "id_usuario_registro", nullable = false)
	private Usuario usuarioRegistro;

	@Column(name = "cantidad_consumida", nullable = false, precision = 14, scale = 4)
	private BigDecimal cantidadConsumida;

	@Column(name = "fecha_consumo", nullable = false)
	private LocalDate fechaConsumo;

	@Column(nullable = false, length = 7)
	private String periodo;

	@Column(name = "creado_en", nullable = false)
	private LocalDateTime creadoEn;

	@PrePersist
	void prePersist() {
		if (creadoEn == null) {
			creadoEn = LocalDateTime.now();
		}
	}
}
