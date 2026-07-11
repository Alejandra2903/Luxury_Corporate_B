package com.example.luxury.dominios.umbral.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.luxury.dominios.common.enums.EstadoRegistro;
import com.example.luxury.dominios.common.exception.ResourceNotFoundException;
import com.example.luxury.dominios.recurso.service.TipoRecursoService;
import com.example.luxury.dominios.sede.service.SedeService;
import com.example.luxury.dominios.umbral.dto.UmbralRequest;
import com.example.luxury.dominios.umbral.dto.UmbralResponse;
import com.example.luxury.dominios.umbral.model.Umbral;
import com.example.luxury.dominios.umbral.repository.UmbralRepository;

@Service
public class UmbralService {

	@Autowired
	private UmbralRepository umbralRepository;

	@Autowired
	private SedeService sedeService;

	@Autowired
	private TipoRecursoService tipoRecursoService;

	public List<UmbralResponse> listar() {
		return umbralRepository.findAll().stream().map(UmbralResponse::from).toList();
	}

	public UmbralResponse crear(UmbralRequest request) {
		Umbral umbral = new Umbral();
		aplicar(umbral, request);
		return UmbralResponse.from(umbralRepository.save(umbral));
	}

	public UmbralResponse actualizar(Long id, UmbralRequest request) {
		Umbral umbral = buscar(id);
		aplicar(umbral, request);
		return UmbralResponse.from(umbralRepository.save(umbral));
	}

	public void eliminar(Long id) {
		Umbral umbral = buscar(id);
		umbral.setEstado(EstadoRegistro.INACTIVO);
		umbralRepository.save(umbral);
	}

	public Umbral buscar(Long id) {
		Optional<Umbral> umbralOptional = umbralRepository.findById(id);
		if (umbralOptional.isPresent()) {
			return umbralOptional.get();
		}
		throw new ResourceNotFoundException("Umbral no encontrado");
	}

	public Umbral buscarVigente(Long sedeId, Long tipoRecursoId, LocalDate fecha) {
		Optional<Umbral> umbralOptional = umbralRepository.findVigente(sedeId, tipoRecursoId, fecha, EstadoRegistro.ACTIVO);
		if (umbralOptional.isPresent()) {
			return umbralOptional.get();
		}
		return null;
	}

	private void aplicar(Umbral umbral, UmbralRequest request) {
		umbral.setSede(sedeService.buscar(request.getSedeId()));
		umbral.setTipoRecurso(tipoRecursoService.buscar(request.getTipoRecursoId()));
		umbral.setLimiteConsumo(request.getLimiteConsumo());
		umbral.setLimitePresupuestoPen(request.getLimitePresupuestoPen());
		umbral.setFechaInicio(request.getFechaInicio());
		umbral.setFechaFin(request.getFechaFin());
		umbral.setEstado(request.getEstado() == null ? EstadoRegistro.ACTIVO : request.getEstado());
	}
}
