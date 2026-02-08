package com.example.BlackboxBackend.Config;

import com.example.BlackboxBackend.DTO.JwtDTO;
import com.example.BlackboxBackend.DTO.RoleEnum;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class PermissionHandler extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String url = request.getRequestURI();
        if (!(url.contains("admin") || url.contains("protected"))) {
            filterChain.doFilter(request, response);
            return;
        }

        SecurityContext securityContextHolder = SecurityContextHolder.getContext();
        Authentication authentication = securityContextHolder.getAuthentication();
        JwtDTO jwtDTO = (JwtDTO) authentication.getPrincipal();

        if(jwtDTO.isSuperAdmin()){
            filterChain.doFilter(request, response);
        }

        if(url.contains("/department")){
            if(jwtDTO.getRole() == RoleEnum.SUPER_ADMIN){
                filterChain.doFilter(request, response);
            }else{
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
    }
}
