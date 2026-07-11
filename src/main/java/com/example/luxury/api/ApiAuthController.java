package com.example.luxury.api;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public Map<String, Object> login(@RequestBody LoginApiRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.identificador(), request.contrasena()));

        Usuario usuario = usuarioRepository.buscarPorIdentificador(request.identificador())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("token", tokenService.generarToken(usuario));
        data.put("tipo", "Bearer");
        data.put("usuario", ApiMapper.usuario(usuario));
        data.put("expiraEnSegundos", tokenService.obtenerExpiracionSegundos());
        return data;
    }

    @PostMapping("/registro")
    public Map<String, Object> registro(@RequestBody RegistroApiRequest request) {
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
        return ApiMapper.usuario(gestionUsuarioService.crear(usuarioRequest));
    }

    public record LoginApiRequest(String identificador, String contrasena) {
    }

    public record RegistroApiRequest(String nombres, String apellidos, String tipoDocumento, String numeroDocumento,
            String telefono, String correo, String contrasena) {
    }
}
