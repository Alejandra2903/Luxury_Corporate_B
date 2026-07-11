package com.example.luxury.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private com.example.luxury.dominios.seguridad.security.FiltroJwt filtroJwt;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/auth/**", "/css/**", "/error", "/login", "/registro").permitAll()
						.requestMatchers("/usuarios/**").hasRole("ADMIN")
						.requestMatchers("/auditorias/**", "/eventos-acceso/**").hasAnyRole("ADMIN", "AUDITOR")
						.requestMatchers("/consumos/**").hasAnyRole("ADMIN", "ANALISTA", "GERENTE")
						.requestMatchers("/", "/dashboard/**", "/reportes/**").hasAnyRole("ADMIN", "GERENTE", "AUDITOR", "ANALISTA")
						.anyRequest().authenticated())
				.exceptionHandling(ex -> ex
						.authenticationEntryPoint((request, response, authException) -> response.sendRedirect("/auth/login")))
				.addFilterBefore(filtroJwt, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/auth/login?logout")
						.deleteCookies("tokenAcceso")
						.permitAll())
				.authenticationProvider(authenticationProvider());
		return http.build();
	}

	@Bean
	AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
