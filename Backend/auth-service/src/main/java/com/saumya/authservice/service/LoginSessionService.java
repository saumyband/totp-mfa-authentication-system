package com.saumya.authservice.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginSessionService {
    // Why ConcurrentHashMap?:
    /**
     *  This application can have one or more users logging in simultaneously.
     *  A normal HashMap is not Thread-safe.
     *  Hence, ConcurrentHashMap is safer "thread-safe" choice
     * */
    private final Map<String, String> loginSessions = new ConcurrentHashMap<>();

    /**
     * @param email
     * @return sessionId
     * @implNote Notes down sessionId with email with each login. Generates UUID & stores it with email in CHMap
     */
    public String createSession(String email) {
        String sessionId = UUID.randomUUID().toString();

        loginSessions.put(
                sessionId,
                email
        );

        return sessionId;
    }

    /**
     * @param sessionId (UUID)
     * @return email
     * @implNote Find email for session
     */
    public String getEmail(String sessionId) {
        return loginSessions.get(sessionId);
    }

    /**
     * @param sessionId
     * @implNote Removes the session with sessionId. When OTP is verified, delete temporary session.
     * Suppose TOTP is verified & JWT generated. No need to keep loginSessionId in memory.
     */
    public void removeSession(String sessionId) {
        loginSessions.remove(sessionId);
    }
}
