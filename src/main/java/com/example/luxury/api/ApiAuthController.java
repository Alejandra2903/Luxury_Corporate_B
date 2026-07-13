package com.example.luxury.api;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.example.luxury.api.dto.UsuarioApiResponse;
import com.example.luxury.dominios.seguridad.dto.request.UsuarioRequest;
import com.example.luxury.dominios.seguridad.enums.NombreRol;
import com.example.luxury.dominios.seguridad.enums.TipoDocumento;
import com.example.luxury.dominios.seguridad.models.Usuario;
import com.example.luxury.dominios.seguridad.repositories.UsuarioRepository;
import com.example.luxury.dominios.seguridad.services.GestionUsuarioService;
import com.example.luxury.dominios.seguridad.services.TokenService;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final GestionUsuarioService gestionUsuarioService;
    private final TokenService tokenService;

    public ApiAuthController(AuthenticationManager authenticationManager, UsuarioRepository usuarioRepository,
            GestionUsuarioService gestionUsuarioService, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.gestionUsuarioService = gestionUsuarioService;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody LoginApiRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.identificador(), request.contrasena()));

        Usuario usuario = usuarioRepository.buscarPorIdentificador(request.identificador())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("token", tokenService.generarToken(usuario));
        data.put("tipo", "Bearer");
        data.put("usuario", ApiMapper.usuarioDto(usuario));
        data.put("expiraEnSegundos", tokenService.obtenerExpiracionSegundos());
        return data;
    }

    @PostMapping("/registro")
    public UsuarioApiResponse registro(@Valid @RequestBody RegistroApiRequest request) {
        UsuarioRequest usuarioRequest = new UsuarioRequest(
                request.nombres(),
                request.apellidos(),
                TipoDocumento.valueOf(request.tipoDocumento()),
                request.numeroDocumento(),
                request.telefono(),
                request.correo(),
                request.contrasena(),
                NombreRol.OPERADOR,
                true);
        return ApiMapper.usuarioDto(gestionUsuarioService.crear(usuarioRequest));
    }

    public record LoginApiRequest(
            @NotBlank(message = "El identificador es obligatorio.") String identificador,
            @NotBlank(message = "La contrasena es obligatoria.") String contrasena) {
    }

    public record RegistroApiRequest(
            @NotBlank(message = "Los nombres son obligatorios.") String nombres,
            @NotBlank(message = "Los apellidos son obligatorios.") String apellidos,
            @NotBlank @Pattern(regexp = "DNI|CE|PASAPORTE", message = "Tipo de documento invalido.") String tipoDocumento,
            @NotBlank(message = "El numero de documento es obligatorio.")
            @Pattern(regexp = "\\d{8,20}", message = "El documento debe tener entre 8 y 20 digitos.") String numeroDocumento,
            @NotBlank(message = "El telefono es obligatorio.")
            @Pattern(regexp = "\\d{9}", message = "El telefono debe tener 9 digitos.") String telefono,
            @NotBlank(message = "El correo es obligatorio.")
            @Email(message = "El correo no es valido.") String correo,
            @NotBlank(message = "La contrasena es obligatoria.")
            @Size(min = 6, message = "La contrasena debe tener al menos 6 caracteres.") String contrasena) {
    }
}
