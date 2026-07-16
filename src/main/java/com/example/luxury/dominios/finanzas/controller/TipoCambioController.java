package com.example.luxury.dominios.finanzas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.luxury.dominios.finanzas.dto.TipoCambioForm;
import com.example.luxury.dominios.finanzas.service.TipoCambioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tipos-cambio")
public class TipoCambioController {

	@Autowired
	private TipoCambioService tipoCambioService;

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("tiposCambio", tipoCambioService.listar());
		model.addAttribute("tipoCambioForm", new TipoCambioForm());
		return "tipos-cambio/lista";
	}

	@PostMapping
	public String crear(
			@Valid @ModelAttribute("tipoCambioForm") TipoCambioForm formulario,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("tiposCambio", tipoCambioService.listar());
			return "tipos-cambio/lista";
		}
		tipoCambioService.crear(formulario.toRequest());
		redirectAttributes.addFlashAttribute("successMessage", "Tipo de cambio registrado correctamente.");
		return "redirect:/tipos-cambio";
	}

	@PostMapping("/{id}/actualizar")
	public String actualizar(
			@PathVariable Long id,
			@Valid @ModelAttribute("tipoCambioForm") TipoCambioForm formulario,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (!bindingResult.hasErrors()) {
			tipoCambioService.actualizar(id, formulario.toRequest());
			redirectAttributes.addFlashAttribute("successMessage", "Tipo de cambio actualizado correctamente.");
		}
		return "redirect:/tipos-cambio";
	}
}
