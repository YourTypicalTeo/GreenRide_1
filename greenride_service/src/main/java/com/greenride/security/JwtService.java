package com.greenride.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    private final String issuer;
    private final String audience;
    private final long ttlMinutes;

    public JwtService(@Value("${greenride.app.jwt.secret}") String secret,
                      @Value("${greenride.app.jwt.issuer}") String issuer,
                      @Value("${greenride.app.jwt.audience}") String audience,
                      @Value("${greenride.app.jwt.ttl-minutes}") long ttlMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
        this.audience = audience;
        this.ttlMinutes = ttlMinutes;
    }

    /**
     * Issues a sophisticated token including Roles, Issuer, and Audience.
     */
    public String issue(String subject, Collection<String> roles) {
        final Instant now = Instant.now();
        return Jwts.builder()
                .subject(subject)
                .issuer(this.issuer)
                .audience().add(this.audience).and() // Strict Audience
                .claim("roles", roles)               // Embed Roles (Stateless)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(Duration.ofMinutes(this.ttlMinutes))))
                .signWith(this.key)
                .compact();
    }

    /**
     * Parses token and validates strict claims.
     */
    public Claims parse(String token) {
        return Jwts.parser()
                .requireAudience(this.audience) // Fail if audience mismatch
                .requireIssuer(this.issuer)     // Fail if issuer mismatch
                .verifyWith(this.key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}