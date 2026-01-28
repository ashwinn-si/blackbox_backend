package com.example.BlackboxBackend.Config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

public class SecurityConfig {
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity){
        httpSecurity
                .csrf((csrf) -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return httpSecurity.build();
    }
}
