package com.example.luxury.dominios.alerta.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.luxury.dominios.alerta.dto.AlertaResponse;
import com.example.luxury.dominios.alerta.service.ReglasAlertasService;

@RestController
@RequestMapping("/alertas")
public class AlertaController {

	@Autowired
	private ReglasAlertasService reglasAlertasService;

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("alertas", reglasAlertasService.listar().stream().map(AlertaResponse::from).toList());
		return "alertas/lista";
	}

	@GetMapping("/sede/{idSede}")
	public String listarPorSede(@PathVariable Long idSede, Model model) {
		model.addAttribute("alertas",
				reglasAlertasService.listarPorSede(idSede).stream().map(AlertaResponse::from).toList());
		return "alertas/lista";
	}

	@GetMapping("/registrar")
	public String mostrarFormulario(Model model) {
		model.addAttribute("alertaForm", new com.example.luxury.dominios.alerta.dto.AlertaForm());
		model.addAttribute("niveles", com.example.luxury.dominios.common.enums.NivelAlerta.values());
		return "alertas/formulario";
	}

	@PostMapping
	public String crear(
			@jakarta.validation.Valid @ModelAttribute("alertaForm") com.example.luxury.dominios.alerta.dto.AlertaForm formulario,
			org.springframework.validation.BindingResult bindingResult,
			RedirectAttributes redirectAttributes,
			Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("niveles", com.example.luxury.dominios.common.enums.NivelAlerta.values());
			return "alertas/formulario";
		}
		reglasAlertasService.crearManual(formulario.getMensaje(), formulario.getNivel());
		redirectAttributes.addFlashAttribute("successMessage", "Alerta manual registrada correctamente.");
		return "redirect:/alertas";
	}

	@PostMapping("/{id}/atender")
	public String atender(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		reglasAlertasService.atender(id);
		redirectAttributes.addFlashAttribute("successMessage", "Alerta atendida correctamente.");
		return "redirect:/alertas";
	}
}
