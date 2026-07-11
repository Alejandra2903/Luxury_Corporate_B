package com.example.luxury.dominios.sede.model;

import com.example.luxury.dominios.common.enums.EstadoRegistro;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "sedes")
public class Sede {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_sede")
	@EqualsAndHashCode.Include
	@ToString.Include
	private Long id;

	@Column(nullable = false, length = 100)
	private String nombre;

	@Column(nullable = false, length = 100)
	private String ciudad;

	@Column(nullable = false, length = 180)
	private String direccion;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EstadoRegistro estado = EstadoRegistro.ACTIVO;
}
