package com.example.BlackboxBackend.Config;

import com.example.BlackboxBackend.DTO.JwtDTO;
import com.example.BlackboxBackend.DTO.RoleEnum;
import com.example.BlackboxBackend.Utils.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtHandler extends OncePerRequestFilter {
  private final JwtService jwtService;

  JwtHandler(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String url = request.getRequestURI();
    if (!(url.contains("admin") || url.contains("protected"))) {
      filterChain.doFilter(request, response);
      return;
    }

    String header = request.getHeader("authorization");

    if (header == null || !header.startsWith("Bearer ")) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    String token = header.replace("Bearer ", "").trim();

    System.out.println(token);
    boolean isValid = jwtService.isValidToken(token);
    if (!isValid) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    Claims claims = jwtService.getClaims(token);
    Long userId = claims.get("userId", Long.class);
    RoleEnum role = claims.get("role", RoleEnum.class);
    boolean isSuperAdmin = claims.get("isSuperAdmin", Boolean.class);
    JwtDTO jwtDTO = new JwtDTO(userId, role, isSuperAdmin);


    String roleName = "ROLE_" + role;
    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
        jwtDTO,
        null,
        List.of(new SimpleGrantedAuthority(roleName)));

    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    filterChain.doFilter(request, response);
  }

}
