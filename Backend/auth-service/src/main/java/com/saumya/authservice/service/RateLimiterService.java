package com.saumya.authservice.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Simple fixed-window, in-memory rate limiter for the sensitive pre-auth
 * endpoints (login, TOTP verify, MFA setup/activate) that are necessarily
 * reachable without a JWT. Keyed by client IP + path so one noisy client
 * can't starve others.
 *
 * Single-instance only — a real multi-node deployment would need a shared
 * store (e.g. Redis) instead of this ConcurrentHashMap.
 */
@Service
public class RateLimiterService {

    private static final int MAX_REQUESTS_PER_WINDOW = 10;
    private static final long WINDOW_MILLIS = 60_000;

    private record Window(long startTime, AtomicInteger count) {}

    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();
    private final AtomicLong lastSweep = new AtomicLong(System.currentTimeMillis());

    public boolean tryAcquire(String key) {
        long now = System.currentTimeMillis();

        sweepExpiredWindowsIfDue(now);

        Window window = windows.compute(key, (k, existing) -> {
            if (existing == null || now - existing.startTime() > WINDOW_MILLIS) {
                return new Window(now, new AtomicInteger(1));
            }

            existing.count().incrementAndGet();

            return existing;
        });

        return window.count().get() <= MAX_REQUESTS_PER_WINDOW;
    }

    // Piggybacks on request traffic instead of a scheduled task, since keys are
    // per client IP+path and would otherwise accumulate in `windows` forever.
    private void sweepExpiredWindowsIfDue(long now) {
        long previousSweep = lastSweep.get();

        if (now - previousSweep <= WINDOW_MILLIS) {
            return;
        }

        if (lastSweep.compareAndSet(previousSweep, now)) {
            windows.values().removeIf(window -> now - window.startTime() > WINDOW_MILLIS);
        }
    }
}
