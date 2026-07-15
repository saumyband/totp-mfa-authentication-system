package com.saumya.authservice.filter;

import tools.jackson.databind.ObjectMapper;
import com.saumya.authservice.service.RateLimiterService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Throttles the pre-auth endpoints that are unavoidably reachable without a
 * JWT (login, TOTP verify, MFA setup/activate), so email/OTP enumeration and
 * brute-force attempts get slowed down instead of running at line rate.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Set<String> LIMITED_PATHS = Set.of(
            "/auth/login",
            "/auth/verify-totp",
            "/auth/mfa/activate"
    );

    private final RateLimiterService rateLimiterService;
    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return !(LIMITED_PATHS.contains(path) || path.startsWith("/auth/mfa/setup/"));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // Bucket every /auth/mfa/setup/{email} request under one logical path so an
        // attacker can't dodge the limit by enumerating a fresh email per request.
        String path = request.getRequestURI();
        String bucket = path.startsWith("/auth/mfa/setup/") ? "/auth/mfa/setup" : path;
        String key = request.getRemoteAddr() + ":" + bucket;

        if (!rateLimiterService.tryAcquire(key)) {
            log.warn("Rate limit exceeded for {} on {}", request.getRemoteAddr(), request.getRequestURI());

            writeJsonError(response, HttpStatus.TOO_MANY_REQUESTS, "Too many requests");
            return;
        }

        filterChain.doFilter(request, response);
    }

    // Keeps error responses shaped like every controller-thrown error
    // ({status, message} JSON via GlobalExceptionHandler), since filter-level
    // rejections happen before a DispatcherServlet exception handler ever runs.
    private void writeJsonError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("message", message);

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
