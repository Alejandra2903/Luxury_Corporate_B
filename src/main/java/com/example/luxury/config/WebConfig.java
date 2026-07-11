package com.example.luxury.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Redirect /login GET requests to /auth/login
        registry.addRedirectViewController("/login", "/auth/login");
        // Redirect /registro GET requests to /auth/registro
        registry.addRedirectViewController("/registro", "/auth/registro");
    }
}
