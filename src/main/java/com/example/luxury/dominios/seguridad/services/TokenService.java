package com.example.luxury.dominios.seguridad.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import com.example.luxury.dominios.seguridad.config.JwtProperties;
import com.example.luxury.dominios.seguridad.models.Usuario;

@Service
public class TokenService {

    private final JwtProperties jwtProperties;
    private Key clave;
    private long expiracionMillis;

    public TokenService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @PostConstruct
    public void init() {
        this.clave = Keys.hmacShaKeyFor(jwtProperties.getSecreto().getBytes(StandardCharsets.UTF_8));
        this.expiracionMillis = jwtProperties.getExpiracionMinutos() * 60 * 1000;
    }

    public String generarToken(Usuario usuario) {
        List<String> roles = usuario.getRoles().stream()
                .map(rol -> rol.getNombre().name())
                .collect(Collectors.toList());

        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expiracionMillis);

        return Jwts.builder()
                .setSubject(String.valueOf(usuario.getId()))
                .claim("roles", roles)
                .setIssuedAt(ahora)
                .setExpiration(expiracion)
                .signWith(clave, SignatureAlgorithm.HS256)
                .compact();
    }

    public Long obtenerIdUsuario(String token) {
        Claims claims = obtenerClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    public boolean esTokenValido(String token) {
        try {
            obtenerClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public long obtenerExpiracionSegundos() {
        return expiracionMillis / 1000;
    }

    private Claims obtenerClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(clave)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
