package com.example.luxury.dominios.alerta.service;

import java.math.BigDecimal;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.luxury.dominios.alerta.model.Alerta;
import com.example.luxury.dominios.alerta.repository.AlertaRepository;
import com.example.luxury.dominios.common.enums.EstadoAlerta;
import com.example.luxury.dominios.common.enums.NivelAlerta;
import com.example.luxury.dominios.common.enums.TipoAlerta;
import com.example.luxury.dominios.common.exception.ResourceNotFoundException;
import com.example.luxury.dominios.consumo.model.Consumo;
import com.example.luxury.dominios.umbral.model.Umbral;
import com.example.luxury.dominios.umbral.service.UmbralService;

@Service
public class ReglasAlertasService {

	@Autowired
	private UmbralService umbralService;

	@Autowired
	private AlertaRepository alertaRepository;

	public List<Alerta> evaluarYGenerar(Consumo consumo, BigDecimal costoPen) {
		Umbral umbral = umbralService.buscarVigente(
				consumo.getSede().getId(),
				consumo.getTipoRecurso().getId(),
				consumo.getFechaConsumo());
		if (umbral == null) {
			return List.of();
		}
		List<Alerta> alertas = new ArrayList<>();
		if (umbral.getLimiteConsumo() != null
				&& consumo.getCantidadConsumida().compareTo(umbral.getLimiteConsumo()) > 0) {
			alertas.add(crear(consumo, umbral, TipoAlerta.EXCESO_CONSUMO,
					"El consumo supera el limite configurado", NivelAlerta.ALTA));
		}
		if (umbral.getLimitePresupuestoPen() != null && costoPen.compareTo(umbral.getLimitePresupuestoPen()) > 0) {
			alertas.add(crear(consumo, umbral, TipoAlerta.EXCESO_PRESUPUESTO,
					"El costo PEN supera el presupuesto configurado", NivelAlerta.CRITICA));
		}
		return alertaRepository.saveAll(alertas);
	}

	public List<Alerta> listar() {
		return alertaRepository.findAll();
	}

	public List<Alerta> listarPorSede(Long sedeId) {
		return alertaRepository.findByConsumoSedeId(sedeId);
	}

	public Alerta atender(Long id) {
		Optional<Alerta> alertaOptional = alertaRepository.findById(id);
		if (alertaOptional.isEmpty()) {
			throw new ResourceNotFoundException("Alerta no encontrada");
		}
		Alerta alerta = alertaOptional.get();
		alerta.setEstado(EstadoAlerta.ATENDIDA);
		return alertaRepository.save(alerta);
	}

	private Alerta crear(Consumo consumo, Umbral umbral, TipoAlerta tipo, String mensaje, NivelAlerta nivel) {
		Alerta alerta = new Alerta();
		alerta.setConsumo(consumo);
		alerta.setUmbral(umbral);
		alerta.setTipoAlerta(tipo);
		alerta.setMensaje(mensaje);
		alerta.setNivel(nivel);
		return alerta;
	}

	public Alerta crearManual(String mensaje, NivelAlerta nivel) {
		Alerta alerta = new Alerta();
		alerta.setTipoAlerta(TipoAlerta.OTRO);
		alerta.setMensaje(mensaje);
		alerta.setNivel(nivel);
		return alertaRepository.save(alerta);
	}
}
