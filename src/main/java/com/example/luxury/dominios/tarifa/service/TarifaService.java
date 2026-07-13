package com.example.luxury.dominios.tarifa.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.luxury.dominios.common.enums.EstadoRegistro;
import com.example.luxury.dominios.common.exception.ResourceNotFoundException;
import com.example.luxury.dominios.recurso.service.TipoRecursoService;
import com.example.luxury.dominios.sede.service.SedeService;
import com.example.luxury.dominios.tarifa.dto.TarifaRequest;
import com.example.luxury.dominios.tarifa.dto.TarifaResponse;
import com.example.luxury.dominios.tarifa.model.TarifaRecurso;
import com.example.luxury.dominios.tarifa.repository.TarifaRecursoRepository;

@Service
public class TarifaService {

	@Autowired
	private TarifaRecursoRepository tarifaRepository;

	@Autowired
	private SedeService sedeService;

	@Autowired
	private TipoRecursoService tipoRecursoService;

	public List<TarifaResponse> listar() {
		return tarifaRepository.findAll().stream().map(TarifaResponse::from).toList();
	}

	public TarifaResponse crear(TarifaRequest request) {
		TarifaRecurso tarifa = new TarifaRecurso();
		aplicar(tarifa, request);
		return TarifaResponse.from(tarifaRepository.save(tarifa));
	}

	public TarifaResponse actualizar(Long id, TarifaRequest request) {
		TarifaRecurso tarifa = buscar(id);
		aplicar(tarifa, request);
		return TarifaResponse.from(tarifaRepository.save(tarifa));
	}

	public TarifaResponse obtenerVigente(Long sedeId, Long tipoRecursoId) {
		return TarifaResponse.from(buscarVigente(sedeId, tipoRecursoId, LocalDate.now()));
	}

	public void eliminar(Long id) {
		TarifaRecurso tarifa = buscar(id);
		tarifa.setEstado(EstadoRegistro.INACTIVO);
		tarifaRepository.save(tarifa);
	}

	public TarifaRecurso buscar(Long id) {
		Optional<TarifaRecurso> tarifaOptional = tarifaRepository.findById(id);
		if (tarifaOptional.isPresent()) {
			return tarifaOptional.get();
		}
		throw new ResourceNotFoundException("Tarifa no encontrada");
	}

	public TarifaRecurso buscarVigente(Long sedeId, Long tipoRecursoId, LocalDate fecha) {
		Optional<TarifaRecurso> tarifaOptional = tarifaRepository.findVigente(sedeId, tipoRecursoId, fecha, EstadoRegistro.ACTIVO);
		if (tarifaOptional.isPresent()) {
			return tarifaOptional.get();
		}
		throw new ResourceNotFoundException("No existe tarifa vigente para la sede y recurso");
	}

	private void aplicar(TarifaRecurso tarifa, TarifaRequest request) {
		tarifa.setSede(sedeService.buscar(request.getSedeId()));
		tarifa.setTipoRecurso(tipoRecursoService.buscar(request.getTipoRecursoId()));
		tarifa.setPrecioUnitarioPen(request.getPrecioUnitarioPen());
		tarifa.setFechaInicio(request.getFechaInicio());
		tarifa.setFechaFin(request.getFechaFin());
		tarifa.setEstado(request.getEstado() == null ? EstadoRegistro.ACTIVO : request.getEstado());
	}
}
