package com.example.luxury.dominios.recurso.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "tipos_recurso")
public class TipoRecurso {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_tipo_recurso")
	@EqualsAndHashCode.Include
	@ToString.Include
	private Long id;

	@Column(nullable = false, unique = true, length = 80)
	private String nombre;

	@Column(name = "unidad_medida", nullable = false, length = 20)
	private String unidadMedida;
}
