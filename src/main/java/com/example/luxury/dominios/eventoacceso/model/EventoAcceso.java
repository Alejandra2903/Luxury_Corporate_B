package com.example.luxury.dominios.eventoacceso.model;

import java.time.LocalDateTime;

import com.example.luxury.dominios.common.enums.TipoEventoAcceso;
import com.example.luxury.dominios.seguridad.models.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "eventos_acceso")
public class EventoAcceso {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_evento")
	@EqualsAndHashCode.Include
	@ToString.Include
	private Long id;

	@ManyToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;

	@Column(name = "email_intentado", length = 160)
	private String emailIntentado;

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_evento", nullable = false, length = 40)
	private TipoEventoAcceso tipoEvento;

	@Column(nullable = false, length = 300)
	private String descripcion;

	@Column(nullable = false)
	private LocalDateTime fecha;

	@Column(length = 60)
	private String ip;

	@PrePersist
	void prePersist() {
		if (fecha == null) {
			fecha = LocalDateTime.now();
		}
	}
}
