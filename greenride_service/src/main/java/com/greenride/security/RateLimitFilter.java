package com.greenride.security;

import com.greenride.service.RateLimitingService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Intercepts all incoming requests to enforce rate limits.
 * Runs before other logic to protect the server from spam.
 */
@Component
@Order(1) // Critical: Run this extremely early in the chain
public class RateLimitFilter extends OncePerRequestFilter {

    @Autowired
    private RateLimitingService rateLimitingService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Identify the client (by IP Address)
        String clientIp = request.getRemoteAddr();

        // 2. Get their bucket
        Bucket bucket = rateLimitingService.resolveBucket(clientIp);

        // 3. Try to take 1 token
        if (bucket.tryConsume(1)) {
            // Success: Let the request proceed
            filterChain.doFilter(request, response);
        } else {
            // Failure: Reject with 429 Too Many Requests
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Too many requests - Please try again later.\"}");
        }
    }
}