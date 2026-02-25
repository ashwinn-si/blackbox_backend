package com.example.BlackboxBackend.Config;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  // TODO: move to env later
  private static final int SALT_VALUE = 12;

  private final PermissionHandler permissionHandler;
  private final JwtHandler jwtHandler;

  public SecurityConfig(PermissionHandler permissionHandler,
      JwtHandler jwtHandler) {
    this.permissionHandler = permissionHandler;
    this.jwtHandler = jwtHandler;
  }

  /* ---------------- Password Encoder ---------------- */

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(SALT_VALUE);
  }

  /* ---------------- Security Filter Chain ---------------- */

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http
        /* Disable CSRF (needed for JWT APIs) */
        .csrf(csrf -> csrf.disable())

        /* Enable CORS */
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))

        /* Authorization rules */
        .authorizeHttpRequests(auth -> auth

            /* Allow preflight */
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

            /* Protect admin routes */
            .requestMatchers("/api/*/admin/**").authenticated()

            /* Everything else */
            .anyRequest().permitAll())

        /* JWT + Permission Filters */
        .addFilterBefore(jwtHandler, UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(permissionHandler, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  /* ---------------- CORS Configuration ---------------- */

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {

    CorsConfiguration config = new CorsConfiguration();

    /* Allow cookies / auth headers */
    config.setAllowCredentials(true);

    /* Frontend origins */
    config.setAllowedOrigins(List.of(
        "http://localhost:5173"
    // "https://yourdomain.com" // prod later
    ));

    /* Allow all headers */
    config.setAllowedHeaders(List.of("*"));

    /* Allow all HTTP methods */
    config.setAllowedMethods(List.of(
        "GET",
        "POST",
        "PUT",
        "DELETE",
        "PATCH",
        "OPTIONS"));

    /* Cache preflight for 1 hour */
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration("/**", config);

    return source;
  }
}