package com.example.luxury.dominios.auditoria.model;

import java.time.LocalDateTime;

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
@Table(name = "auditorias")
public class Auditoria {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_auditoria")
	@EqualsAndHashCode.Include
	@ToString.Include
	private Long id;

	@ManyToOne
	@JoinColumn(name = "id_usuario", nullable = false)
	private Usuario usuario;

	@Column(nullable = false, length = 80)
	private String modulo;

	@Column(nullable = false, length = 80)
	private String accion;

	@Column(name = "tabla_afectada", length = 80)
	private String tablaAfectada;

	@Column(name = "id_registro_afectado")
	private Long idRegistroAfectado;

	@Column(nullable = false, length = 500)
	private String descripcion;

	@Column(nullable = false)
	private LocalDateTime fecha;

	@PrePersist
	void prePersist() {
		if (fecha == null) {
			fecha = LocalDateTime.now();
		}
	}
}
