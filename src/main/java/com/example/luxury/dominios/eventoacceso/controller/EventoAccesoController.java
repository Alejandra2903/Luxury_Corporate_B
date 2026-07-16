package com.example.luxury.dominios.eventoacceso.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.luxury.dominios.eventoacceso.service.EventoAccesoService;

@RestController
@RequestMapping("/eventos-acceso")
public class EventoAccesoController {

	@Autowired
	private EventoAccesoService eventoAccesoService;

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("eventos", eventoAccesoService.listar());
		return "eventos-acceso/lista";
	}
}
