/*package com.example.backend.sla.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(
    proxyBeanMethods = false
)
@Profile("dev")
public class SlaDevSecurityConfig {
    
    @Bean
    SecurityFilterChain slaDevSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(authorize -> authorize.requestMatchers("/api/admin/sla/**").permitAll().anyRequest().authenticated());
        return http.build();
    }
}*/
