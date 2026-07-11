package com.example.luxury.dominios.seguridad.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "seguridad.jwt")
public class JwtProperties {

    private String secreto;
    private long expiracionMinutos;

    public String getSecreto() {
        return secreto;
    }

    public void setSecreto(String secreto) {
        this.secreto = secreto;
    }

    public long getExpiracionMinutos() {
        return expiracionMinutos;
    }

    public void setExpiracionMinutos(long expiracionMinutos) {
        this.expiracionMinutos = expiracionMinutos;
    }
}
