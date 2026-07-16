package com.example.luxury.dominios.sede.controller;

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

import com.example.luxury.dominios.sede.dto.SedeForm;
import com.example.luxury.dominios.sede.dto.SedeResponse;
import com.example.luxury.dominios.sede.service.SedeService;

import jakarta.validation.Valid;

import com.example.luxury.dominios.recurso.service.TipoRecursoService;
import com.example.luxury.dominios.recurso.dto.TipoRecursoForm;

@RestController
@RequestMapping("/sedes")
public class SedeController {

	@Autowired
	private SedeService sedeService;
	
	@Autowired
	private TipoRecursoService tipoRecursoService;

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("sedes", sedeService.listar());
		model.addAttribute("tiposRecurso", tipoRecursoService.listar());
		model.addAttribute("tipoRecursoForm", new TipoRecursoForm());
		return "sedes/lista";
	}

	@GetMapping("/registrar")
	public String mostrarFormulario(Model model) {
		model.addAttribute("sedeForm", new SedeForm());
		return "sedes/formulario";
	}

	@PostMapping
	public String crear(
			@Valid @ModelAttribute("sedeForm") SedeForm formulario,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "sedes/formulario";
		}
		sedeService.crear(formulario.toRequest());
		redirectAttributes.addFlashAttribute("successMessage", "Sede registrada correctamente.");
		return "redirect:/sedes";
	}

	@GetMapping("/{id}/editar")
	public String mostrarEditar(@PathVariable Long id, Model model) {
		SedeResponse sede = sedeService.obtener(id);
		SedeForm form = new SedeForm();
		form.setNombre(sede.getNombre());
		form.setCiudad(sede.getCiudad());
		form.setDireccion(sede.getDireccion());
		form.setEstado(sede.getEstado());
		model.addAttribute("sedeForm", form);
		model.addAttribute("sedeId", id);
		return "sedes/formulario";
	}

	@PostMapping("/{id}/editar")
	public String actualizar(
			@PathVariable Long id,
			@Valid @ModelAttribute("sedeForm") SedeForm formulario,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("sedeId", id);
			return "sedes/formulario";
		}
		sedeService.actualizar(id, formulario.toRequest());
		redirectAttributes.addFlashAttribute("successMessage", "Sede actualizada correctamente.");
		return "redirect:/sedes";
	}

	@PostMapping("/{id}/eliminar")
	public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		sedeService.eliminar(id);
		redirectAttributes.addFlashAttribute("successMessage", "Sede desactivada correctamente.");
		return "redirect:/sedes";
	}
}
