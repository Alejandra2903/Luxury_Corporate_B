package com.example.luxury.dominios.recurso.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.luxury.dominios.common.exception.ResourceNotFoundException;
import com.example.luxury.dominios.recurso.dto.TipoRecursoRequest;
import com.example.luxury.dominios.recurso.dto.TipoRecursoResponse;
import com.example.luxury.dominios.recurso.model.TipoRecurso;
import com.example.luxury.dominios.recurso.repository.TipoRecursoRepository;

@Service
public class TipoRecursoService {

	@Autowired
	private TipoRecursoRepository tipoRecursoRepository;

	public List<TipoRecursoResponse> listar() {
		return tipoRecursoRepository.findAll().stream().map(TipoRecursoResponse::from).toList();
	}

	public TipoRecursoResponse crear(TipoRecursoRequest request) {
		TipoRecurso tipo = new TipoRecurso();
		aplicar(tipo, request);
		return TipoRecursoResponse.from(tipoRecursoRepository.save(tipo));
	}

	public TipoRecursoResponse actualizar(Long id, TipoRecursoRequest request) {
		TipoRecurso tipo = buscar(id);
		aplicar(tipo, request);
		return TipoRecursoResponse.from(tipoRecursoRepository.save(tipo));
	}

	public void eliminar(Long id) {
		tipoRecursoRepository.delete(buscar(id));
	}

	public TipoRecurso buscar(Long id) {
		Optional<TipoRecurso> tipoOptional = tipoRecursoRepository.findById(id);
		if (tipoOptional.isPresent()) {
			return tipoOptional.get();
		}
		throw new ResourceNotFoundException("Tipo de recurso no encontrado");
	}

	private void aplicar(TipoRecurso tipo, TipoRecursoRequest request) {
		tipo.setNombre(request.getNombre());
		tipo.setUnidadMedida(request.getUnidadMedida());
	}
}
