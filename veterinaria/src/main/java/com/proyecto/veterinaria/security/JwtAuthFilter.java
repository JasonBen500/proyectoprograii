package com.proyecto.veterinaria.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.proyecto.veterinaria.entity.Usuarios;
import com.proyecto.veterinaria.repository.UsuariosRepository;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.JwtException;

import java.io.IOException;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuariosRepository usuariosRepository;

    public JwtAuthFilter(JwtService jwtService, UsuariosRepository usuariosRepository) {
        this.jwtService = jwtService;
        this.usuariosRepository = usuariosRepository;
    }

    private void responder401(HttpServletResponse response, String mensaje) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"mensaje\":\"" + mensaje + "\"}");
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // 1. Si no trae header o no empieza con "Bearer ", sigue de largo
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String usuario = jwtService.extraerUsuario(token);

            if (usuario != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtService.tokenValido(token, usuario)) {
                    // El usuario y su perfil se leen de la BD: si lo desactivan o le cambian
                    // el perfil, el cambio aplica de inmediato aunque el token siga vigente
                    Usuarios u = usuariosRepository.findByUsuario(usuario).orElse(null);
                    if (u == null || !Boolean.TRUE.equals(u.getEstado()) || u.getIdPerfil() == null) {
                        responder401(response, "Sesión inválida o usuario inactivo");
                        return;
                    }
                    String rol = JwtService.normalizarRol(u.getIdPerfil().getNombrePerfil());
                    List<SimpleGrantedAuthority> permisos = (rol == null || rol.isBlank())
                            ? List.of()
                            : List.of(new SimpleGrantedAuthority("ROLE_" + rol));
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            usuario, null, permisos);
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (JwtException | IllegalArgumentException e) {
            responder401(response, "Sesión expirada o token inválido");
            return;
        }

        filterChain.doFilter(request, response);
    }
}