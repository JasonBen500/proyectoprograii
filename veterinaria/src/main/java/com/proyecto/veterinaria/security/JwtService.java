package com.proyecto.veterinaria.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationMs;

    private SecretKey obtenerClave() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // Genera el token para un usuario
    public String generarToken(String usuario, Integer idUsuario, Integer idPerfil) {
        return Jwts.builder()
                .subject(usuario)
                .claim("idUsuario", idUsuario)
                .claim("idPerfil", idPerfil)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(obtenerClave())
                .compact();
    }

    // Extrae el nombre de usuario del token
    public String extraerUsuario(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    public boolean tokenValido(String token, String usuario) {
        final String usuarioDelToken = extraerUsuario(token);
        return usuarioDelToken.equals(usuario) && !tokenExpirado(token);
    }

    private boolean tokenExpirado(String token) {
        return extraerClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extraerClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser()
                .verifyWith(obtenerClave())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return resolver.apply(claims);
    }
}