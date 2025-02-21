package com.lolgap.project.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.lolgap.project.security.JwtUtil;
import com.lolgap.project.security.JwtAuthenticationFilter;


@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Configuration de la sécurité
        http.csrf().disable()
            .authorizeRequests()
            .requestMatchers("/login", "/register").permitAll()  // Routes accessibles sans authentification
            .anyRequest().authenticated(); // Toutes les autres routes nécessitent une authentification

        // Ajout du filtre JWT avant le filtre d'authentification par défaut
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();  // On doit renvoyer la configuration
    }
}
