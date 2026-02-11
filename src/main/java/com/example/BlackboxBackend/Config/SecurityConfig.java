package com.example.BlackboxBackend.Config;

import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  // TODO need to import from the env later
  private int SALT_VALUE = 12;
  private final PermissionHandler permissionHandler;
  private final JwtHandler jwtHandler;

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(SALT_VALUE);
  }

  SecurityConfig(PermissionHandler permissionHandler, JwtHandler jwtHandler){
    this.permissionHandler = permissionHandler;
    this.jwtHandler = jwtHandler;
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .csrf((csrf) -> csrf.disable())
        .authorizeHttpRequests(auth -> auth.requestMatchers("/api/*/admin/**").authenticated()
            .anyRequest().permitAll())
        .addFilterBefore(jwtHandler, UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(permissionHandler, UsernamePasswordAuthenticationFilter.class);
    return httpSecurity.build();
  }
  
  @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
