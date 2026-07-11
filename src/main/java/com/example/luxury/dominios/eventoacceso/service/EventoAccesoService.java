package com.example.luxury.dominios.eventoacceso.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.luxury.dominios.common.enums.TipoEventoAcceso;
import com.example.luxury.dominios.eventoacceso.dto.EventoAccesoResponse;
import com.example.luxury.dominios.eventoacceso.model.EventoAcceso;
import com.example.luxury.dominios.eventoacceso.repository.EventoAccesoRepository;
import com.example.luxury.dominios.seguridad.models.Usuario;

@Service
public class EventoAccesoService {

	@Autowired
	private EventoAccesoRepository eventoAccesoRepository;

	public void registrar(Usuario usuario, String emailIntentado, TipoEventoAcceso tipoEvento, String descripcion, String ip) {
		EventoAcceso evento = new EventoAcceso();
		evento.setUsuario(usuario);
		evento.setEmailIntentado(emailIntentado);
		evento.setTipoEvento(tipoEvento);
		evento.setDescripcion(descripcion);
		evento.setIp(ip);
		eventoAccesoRepository.save(evento);
	}

	public List<EventoAccesoResponse> listar() {
		return eventoAccesoRepository.findAll().stream().map(EventoAccesoResponse::from).toList();
	}
}
