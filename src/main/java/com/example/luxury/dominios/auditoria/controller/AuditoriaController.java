package com.example.luxury.dominios.auditoria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.luxury.dominios.auditoria.service.AuditoriaService;

@Controller
@RequestMapping("/auditorias")
public class AuditoriaController {

	@Autowired
	private AuditoriaService auditoriaService;

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("auditorias", auditoriaService.listar());
		return "auditorias/lista";
	}

	@GetMapping("/usuario/{idUsuario}")
	public String listarPorUsuario(@PathVariable Long idUsuario, Model model) {
		model.addAttribute("auditorias", auditoriaService.listarPorUsuario(idUsuario));
		return "auditorias/lista";
	}

	@GetMapping("/modulo/{modulo}")
	public String listarPorModulo(@PathVariable String modulo, Model model) {
		model.addAttribute("auditorias", auditoriaService.listarPorModulo(modulo));
		return "auditorias/lista";
	}
}
