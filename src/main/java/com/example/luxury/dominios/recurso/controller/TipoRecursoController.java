package com.example.luxury.dominios.recurso.controller;

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

import com.example.luxury.dominios.recurso.dto.TipoRecursoForm;
import com.example.luxury.dominios.recurso.service.TipoRecursoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tipos-recurso")
public class TipoRecursoController {

	@Autowired
	private TipoRecursoService tipoRecursoService;

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("tiposRecurso", tipoRecursoService.listar());
		return "tipos-recurso/lista";
	}

	@GetMapping("/registrar")
	public String mostrarFormulario(Model model) {
		model.addAttribute("tipoRecursoForm", new TipoRecursoForm());
		return "tipos-recurso/formulario";
	}

	@PostMapping
	public String crear(
			@Valid @ModelAttribute("tipoRecursoForm") TipoRecursoForm formulario,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "redirect:/sedes";
		}
		tipoRecursoService.crear(formulario.toRequest());
		redirectAttributes.addFlashAttribute("successMessage", "Tipo de recurso registrado correctamente.");
		return "redirect:/sedes";
	}

	@PostMapping("/{id}/eliminar")
	public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		tipoRecursoService.eliminar(id);
		redirectAttributes.addFlashAttribute("successMessage", "Tipo de recurso eliminado correctamente.");
		return "redirect:/sedes";
	}
}
