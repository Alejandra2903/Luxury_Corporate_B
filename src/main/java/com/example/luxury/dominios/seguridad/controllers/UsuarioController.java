package com.example.luxury.dominios.seguridad.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.luxury.dominios.seguridad.dto.request.UsuarioForm;
import com.example.luxury.dominios.seguridad.dto.response.UsuarioResponse;
import com.example.luxury.dominios.seguridad.enums.NombreRol;
import com.example.luxury.dominios.seguridad.enums.TipoDocumento;
import com.example.luxury.dominios.seguridad.services.GestionUsuarioService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final GestionUsuarioService gestionUsuarioService;

    public UsuarioController(GestionUsuarioService gestionUsuarioService) {
        this.gestionUsuarioService = gestionUsuarioService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String estado, Model model) {
        model.addAttribute("usuarios", gestionUsuarioService.listar(estado));
        model.addAttribute("estadoSeleccionado", estado);
        return "usuarios/lista";
    }

    @GetMapping("/{id}")
    public String obtener(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", gestionUsuarioService.obtener(id));
        return "usuarios/detalle";
    }

    @GetMapping("/registrar")
    public String mostrarFormulario(Model model) {
        agregarCatalogos(model);
        model.addAttribute("usuarioForm", new UsuarioForm());
        return "usuarios/formulario";
    }

    @PostMapping
    public String crear(
            @Valid @ModelAttribute("usuarioForm") UsuarioForm formulario,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            agregarCatalogos(model);
            return "usuarios/formulario";
        }
        try {
            gestionUsuarioService.crear(formulario.toRequest());
            redirectAttributes.addFlashAttribute("successMessage", "Usuario registrado correctamente.");
            return "redirect:/usuarios";
        } catch (IllegalArgumentException ex) {
            agregarCatalogos(model);
            model.addAttribute("errorMessage", ex.getMessage());
            return "usuarios/formulario";
        }
    }

    @GetMapping("/{id}/editar")
    public String mostrarEditar(@PathVariable Long id, Model model) {
        UsuarioResponse usuario = gestionUsuarioService.obtener(id);
        UsuarioForm form = new UsuarioForm();
        form.setNombres(usuario.getNombres());
        form.setApellidos(usuario.getApellidos());
        form.setTipoDocumento(usuario.getTipoDocumento());
        form.setNumeroDocumento(usuario.getNumeroDocumento());
        form.setTelefono(usuario.getTelefono());
        form.setCorreo(usuario.getCorreo());
        form.setRol(usuario.getRoles().isBlank() ? null : NombreRol.valueOf(usuario.getRoles().split(", ")[0]));
        form.setActivo(usuario.isActivo());

        agregarCatalogos(model);
        model.addAttribute("usuarioForm", form);
        model.addAttribute("usuarioId", id);
        return "usuarios/formulario";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(
            @PathVariable Long id,
            @Valid @ModelAttribute("usuarioForm") UsuarioForm formulario,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            agregarCatalogos(model);
            model.addAttribute("usuarioId", id);
            return "usuarios/formulario";
        }
        try {
            gestionUsuarioService.actualizar(id, formulario.toRequest());
            redirectAttributes.addFlashAttribute("successMessage", "Usuario actualizado correctamente.");
            return "redirect:/usuarios";
        } catch (IllegalArgumentException ex) {
            agregarCatalogos(model);
            model.addAttribute("usuarioId", id);
            model.addAttribute("errorMessage", ex.getMessage());
            return "usuarios/formulario";
        }
    }

    @PostMapping("/{id}/toggle-estado")
    public String cambiarEstado(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        gestionUsuarioService.cambiarEstado(id);
        redirectAttributes.addFlashAttribute("successMessage", "Estado del usuario actualizado correctamente.");
        return "redirect:/usuarios";
    }

    private void agregarCatalogos(Model model) {
        model.addAttribute("roles", NombreRol.values());
        model.addAttribute("tiposDocumento", TipoDocumento.values());
    }
}
