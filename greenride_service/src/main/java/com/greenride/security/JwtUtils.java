package com.greenride.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * Utility class for handling JWT operations:
 * - Generation of tokens
 * - Validation of tokens
 * - Parsing information from tokens
 */
@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // Reads the secret key from application.properties
    @Value("${greenride.app.jwtSecret}")
    private String jwtSecret;

    // Reads the expiration time from application.properties
    @Value("${greenride.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    /**
     * Generates a new JWT token from a user's Authentication object.
     *
     * @param authentication The user's authentication object.
     * @return A String representing the JWT.
     */
    public String generateJwtToken(Authentication authentication) {

        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername()) // Set the username as the "subject"
                .setIssuedAt(new Date()) // Set the creation date
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Set the expiration date
                .signWith(key(), SignatureAlgorithm.HS512) // Sign it with our secret key
                .compact();
    }

    /**
     * Creates the signing Key object from the secret string.
     */
    private Key key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Gets the username from a JWT token.
     *
     * @param token The JWT token string.
     * @return The username.
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Validates a JWT token.
     * Checks signature, expiration, and format.
     *
     * @param authToken The JWT token string to validate.
     * @return true if the token is valid, false otherwise.
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}