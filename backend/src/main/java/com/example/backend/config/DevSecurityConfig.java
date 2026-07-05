package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("dev")
public class DevSecurityConfig {
    @Bean
    SecurityFilterChain securityFIlterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
        .cors(Customizer.withDefaults())
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(
                "/api/admin/sla/**",
                            "/api/agent/tickets/**"
            ).permitAll()
            .anyRequest().authenticated())
        .build();
    }
}
