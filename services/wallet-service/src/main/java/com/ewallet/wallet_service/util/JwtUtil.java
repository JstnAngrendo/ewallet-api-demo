package com.ewallet.wallet_service.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKeyString;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }
    private final long EXPIRATION_TIME = 86400000;

    public String generateToken(UUID userId, String username, String role) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("username", username)
                .claim("roles", "ROLE_" + role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(extractAllClaims(token).getSubject());
    }

    public String extractRole(String token) {
        return (String) extractAllClaims(token).get("roles");
    }
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            throw new SecurityException("Invalid JWT signature");
        } catch (MalformedJwtException e) {
            throw new SecurityException("Invalid JWT token");
        } catch (ExpiredJwtException e) {
            throw new SecurityException("Expired JWT token");
        } catch (UnsupportedJwtException e) {
            throw new SecurityException("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            throw new SecurityException("JWT claims string is empty");
        }
    }

    public long getExpirationInSeconds() {
        return EXPIRATION_TIME / 1000;
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token expired");
        } catch (JwtException e) {
            throw new RuntimeException("Invalid token");
        }
    }

    public String extractUsername(String token) {
        return (String) extractAllClaims(token).get("username");
    }

    public boolean isTokenValid(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
}
