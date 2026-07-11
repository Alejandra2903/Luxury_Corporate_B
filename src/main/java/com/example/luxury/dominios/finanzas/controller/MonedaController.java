package com.example.luxury.dominios.finanzas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.luxury.dominios.finanzas.dto.MonedaForm;
import com.example.luxury.dominios.finanzas.service.MonedaService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/monedas")
public class MonedaController {

	@Autowired
	private MonedaService monedaService;

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("monedas", monedaService.listar());
		model.addAttribute("monedaForm", new MonedaForm());
		return "monedas/lista";
	}

	@PostMapping
	public String crear(
			@Valid @ModelAttribute("monedaForm") MonedaForm formulario,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("monedas", monedaService.listar());
			return "monedas/lista";
		}
		monedaService.crear(formulario.toRequest());
		redirectAttributes.addFlashAttribute("successMessage", "Moneda registrada correctamente.");
		return "redirect:/monedas";
	}
}
