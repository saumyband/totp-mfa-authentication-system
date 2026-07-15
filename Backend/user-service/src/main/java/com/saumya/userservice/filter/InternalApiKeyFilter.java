package com.saumya.userservice.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Collections;

/**
 * user-service is only ever meant to be called by auth-service (server-to-server),
 * except for registration which the Frontend calls directly. Everything else
 * (raw user lookup, MFA enable) must present this shared secret, since it
 * exposes password hashes and encrypted TOTP secrets otherwise.
 */
@Slf4j
@Component
public class InternalApiKeyFilter extends OncePerRequestFilter {

    private static final String HEADER_NAME = "X-Internal-Api-Key";

    @Value("${internal.api-key}")
    private String internalApiKey;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod())
                && request.getRequestURI().equals("/users/register");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String providedKey = request.getHeader(HEADER_NAME);

        if (providedKey == null || !MessageDigest.isEqual(
                providedKey.getBytes(StandardCharsets.UTF_8),
                internalApiKey.getBytes(StandardCharsets.UTF_8))) {
            log.warn(
                    "Rejected request to {} {} — missing/invalid internal API key",
                    request.getMethod(),
                    request.getRequestURI()
            );

            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "auth-service",
                        null,
                        Collections.emptyList()
                )
        );

        filterChain.doFilter(request, response);
    }
}
