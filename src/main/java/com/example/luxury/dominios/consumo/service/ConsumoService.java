package com.example.luxury.dominios.consumo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.luxury.dominios.alerta.service.ReglasAlertasService;
import com.example.luxury.dominios.auditoria.service.AuditoriaService;
import com.example.luxury.dominios.common.exception.ResourceNotFoundException;
import com.example.luxury.dominios.consumo.dto.ConsumoRequest;
import com.example.luxury.dominios.consumo.dto.ConsumoResponse;
import com.example.luxury.dominios.consumo.model.Consumo;
import com.example.luxury.dominios.consumo.repository.ConsumoRepository;
import com.example.luxury.dominios.finanzas.model.ConsumoCosto;
import com.example.luxury.dominios.finanzas.service.ConversionFinancieraService;
import com.example.luxury.dominios.recurso.service.TipoRecursoService;
import com.example.luxury.dominios.seguridad.services.AuthenticatedUserService;
import com.example.luxury.dominios.sede.service.SedeService;
import com.example.luxury.dominios.tarifa.model.TarifaRecurso;
import com.example.luxury.dominios.tarifa.service.TarifaService;
import com.example.luxury.dominios.seguridad.models.Usuario;

@Service
public class ConsumoService {

	@Autowired
	private ConsumoRepository consumoRepository;

	@Autowired
	private SedeService sedeService;

	@Autowired
	private TipoRecursoService tipoRecursoService;

	@Autowired
	private TarifaService tarifaService;

	@Autowired
	private ConversionFinancieraService conversionFinancieraService;

	@Autowired
	private ReglasAlertasService reglasAlertasService;

	@Autowired
	private AuditoriaService auditoriaService;

	@Autowired
	private AuthenticatedUserService authenticatedUserService;

	public ConsumoResponse registrar(ConsumoRequest request) {
		Usuario usuario = authenticatedUserService.actual();
		TarifaRecurso tarifa = tarifaService.buscarVigente(request.getSedeId(), request.getTipoRecursoId(), request.getFechaConsumo());
		Consumo consumo = new Consumo();
		consumo.setSede(sedeService.buscar(request.getSedeId()));
		consumo.setTipoRecurso(tipoRecursoService.buscar(request.getTipoRecursoId()));
		consumo.setTarifa(tarifa);
		consumo.setUsuarioRegistro(usuario);
		consumo.setCantidadConsumida(request.getCantidadConsumida());
		consumo.setFechaConsumo(request.getFechaConsumo());
		consumo.setPeriodo(request.getPeriodo());
		Consumo saved = consumoRepository.save(consumo);
		BigDecimal costoPen = request.getCantidadConsumida()
				.multiply(tarifa.getPrecioUnitarioPen())
				.setScale(4, RoundingMode.HALF_UP);
		List<ConsumoCosto> costos = conversionFinancieraService.calcularYGuardarCostos(saved, costoPen);
		reglasAlertasService.evaluarYGenerar(saved, costoPen);
		auditoriaService.registrar(usuario, "CONSUMOS", "CREAR", "consumos", saved.getId(),
				"Registro de consumo para sede " + saved.getSede().getNombre());
		return ConsumoResponse.from(saved, costos);
	}

	public List<ConsumoResponse> listar() {
		return consumoRepository.findAll().stream().map(this::toResponse).toList();
	}

	public ConsumoResponse obtener(Long id) {
		return toResponse(buscar(id));
	}

	public List<ConsumoResponse> listarPorSede(Long sedeId) {
		return consumoRepository.findBySedeId(sedeId).stream().map(this::toResponse).toList();
	}

	public List<ConsumoResponse> listarPorPeriodo(String periodo) {
		return consumoRepository.findByPeriodo(periodo).stream().map(this::toResponse).toList();
	}

	private Consumo buscar(Long id) {
		Optional<Consumo> consumoOptional = consumoRepository.findById(id);
		if (consumoOptional.isPresent()) {
			return consumoOptional.get();
		}
		throw new ResourceNotFoundException("Consumo no encontrado");
	}

	private ConsumoResponse toResponse(Consumo consumo) {
		return ConsumoResponse.from(consumo, conversionFinancieraService.listarCostos(consumo.getId()));
	}
}
