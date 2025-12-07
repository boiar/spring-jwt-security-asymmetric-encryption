package com.example.auth_security.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    public static final String TOKEN_TYPE = "token_type";
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    @Value("${app.security.jwt.access-token-expiration}")
    private long accessTokenExpiration;
    @Value("${app.security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;


    public JwtService(
            @Value("${app.security.jwt.private-key}") String privateKeyPath,
            @Value("${app.security.jwt.public-key}") String publicKeyPath
    ) throws Exception {
        this.privateKey = KeyUtils.loadPrivateKey(privateKeyPath);
        this.publicKey = KeyUtils.loadPublicKey(publicKeyPath);
    }

    public String generateAccessToken(final String username) {
        final Map<String, Object> claims = Map.of(TOKEN_TYPE, "ACCESS_TOKEN");
        return buildToken(username, claims, this.accessTokenExpiration);
    }

    public String refreshAccessToken(final String refreshToken){
        final Claims claims =extractClaims(refreshToken);

        if (!"REFRESH_TOKEN".equals(claims.get("TOKEN_TYPE", String.class))) {
            throw new RuntimeException("Invalid token type!");
        }

        if (claims.getExpiration().before(new Date())) {
            throw new RuntimeException("Refresh token expired!");
        }

        final String username = claims.getSubject();
        return generateAccessToken(username);
    }

    public String generateRefreshToken(final String username) {
        final Map<String, Object> claims = Map.of(TOKEN_TYPE, "REFRESH_TOKEN");
        return buildToken(username, claims, this.refreshTokenExpiration);
    }



    private String buildToken(String username, Map<String, Object> claims, long expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .compact();
    }

    public boolean isTokenValid(final String token, final String expectedUsername){
        final String username = extractUsername(token);
        return username.equals(expectedUsername) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration()
                .before(new Date());
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    private Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(this.publicKey)
                    .build()
                    .parseEncryptedClaims(token)
                    .getPayload();
        } catch (final JwtException e) {
            throw new RuntimeException("Invalid token", e);
        }
    }

}
