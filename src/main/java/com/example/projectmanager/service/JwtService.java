package com.example.projectmanager.service;

import com.example.projectmanager.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    
    private final String secretKey;
    private final long jwtExpiration;
    private final long refreshExpiration;

    public JwtService(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration}") long jwtExpiration,
            @Value("${jwt.refresh-expiration}") long refreshExpiration) {

        if (secretKey == null || secretKey.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters long");
        }

        this.secretKey = secretKey;
        this.jwtExpiration = jwtExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    // Генерация токена для пользователя
    public String generateToken(User user) {
        return generateToken(new HashMap<>(), user);
    }

    // Генерация токена с дополнительными claims
    public String generateToken(
        Map<String, Object> extraClaims,
        User user
    ) {
        return buildToken(extraClaims, user, jwtExpiration);
    }

    // Генерация refresh-токена
    public String generateRefreshToken(User user) {
        return buildToken(new HashMap<>(), user, refreshExpiration);
    }

    private String buildToken(
        Map<String, Object> extraClaims,
        User user,
        long expiration
    ) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .claim("role", user.getRole())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Извлечение username из токена
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Извлечение роли из токена
    public String extractRole(String token) {
        final Claims claims = extractAllClaims(token);
        return (String) claims.get("role");
    }

    // Извлечение произвольного claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Проверка валидности токена для пользователя
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // Проверка истечения срока действия токена
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Извлечение даты истечения
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Извлечение всех claims из токена
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Получение ключа для подписи
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}