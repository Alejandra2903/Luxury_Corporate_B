package com.example.luxury.dominios.seguridad.controllers;

import java.time.Duration;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import com.example.luxury.dominios.seguridad.dto.request.LoginRequest;
import com.example.luxury.dominios.seguridad.dto.request.RegistroUsuarioRequest;
import com.example.luxury.dominios.seguridad.models.Usuario;
import com.example.luxury.dominios.seguridad.repositories.UsuarioRepository;
import com.example.luxury.dominios.seguridad.services.AutenticacionService;
import com.example.luxury.dominios.seguridad.services.TokenService;

@Controller
@RequestMapping("/auth")
public class GestionAutenticacionController {

    private final AutenticacionService autenticacionService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    public GestionAutenticacionController(AutenticacionService autenticacionService,
            AuthenticationManager authenticationManager,
            TokenService tokenService,
            UsuarioRepository usuarioRepository) {
        this.autenticacionService = autenticacionService;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        if (!model.containsAttribute("loginRequest")) {
            model.addAttribute("loginRequest", new LoginRequest());
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String procesarLogin(
            @Valid @ModelAttribute("loginRequest") LoginRequest request,
            BindingResult bindingResult,
            Model model,
            HttpServletRequest httpRequest,
            HttpServletResponse response) {

        if (bindingResult.hasErrors()) {
            return "auth/login";
        }

        try {
            String identificador = request.getIdentificador().trim();
            var autenticacion = new UsernamePasswordAuthenticationToken(
                    identificador, request.getContrasena());
            authenticationManager.authenticate(autenticacion);

            Usuario usuario = usuarioRepository.buscarPorIdentificador(identificador)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            String token = tokenService.generarToken(usuario);
            boolean seguro = httpRequest.isSecure();
            ResponseCookie cookieJwt = ResponseCookie.from("tokenAcceso", token)
                    .httpOnly(true)
                    .secure(seguro)
                    .sameSite("Lax")
                    .path("/")
                    .maxAge(Duration.ofSeconds(tokenService.obtenerExpiracionSegundos()))
                    .build();
            response.addHeader("Set-Cookie", cookieJwt.toString());

            return "redirect:/dashboard";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Credenciales inválidas.");
            return "auth/login";
        }
    }

    @GetMapping("/logout")
    public String procesarLogout(HttpServletRequest httpRequest, HttpServletResponse response) {
        boolean seguro = httpRequest.isSecure();
        ResponseCookie cookieJwt = ResponseCookie.from("tokenAcceso", "")
                .httpOnly(true)
                .secure(seguro)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ZERO)
                .build();
        response.addHeader("Set-Cookie", cookieJwt.toString());
        return "redirect:/auth/login";
    }

    @GetMapping("/sin-panel")
    public String mostrarSinPanel() {
        return "auth/sin-panel";
    }

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("registroRequest", new RegistroUsuarioRequest());
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registrar(
            @Valid @ModelAttribute("registroRequest") RegistroUsuarioRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "auth/registro";
        }

        try {
            autenticacionService.registrar(request);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Usuario registrado correctamente. Ahora puedes iniciar sesión.");
            return "redirect:/auth/login";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "auth/registro";
        }
    }
}
