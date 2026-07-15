package com.saumya.authservice.service;

import com.saumya.authservice.client.UserServiceClient;
import com.saumya.authservice.dto.*;
import com.saumya.authservice.exception.InvalidCredentialsException;
import com.saumya.authservice.exception.InvalidOtpException;
import com.saumya.authservice.exception.MfaNotEnabledException;
import com.saumya.authservice.util.AesUtil;
import com.saumya.authservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserServiceClient userServiceClient;
    private final LoginSessionService loginSessionService;
    private final AesUtil aesUtil;
    private final JwtUtil jwtUtil;

    public LoginResponse login(
            LoginRequest request) {
        // Getting the user from user-service.
        UserInfoResponse user = userServiceClient.getUser(request.getEmail());

        // Password hash never leaves user-service — it verifies the match for us.
        boolean matches = userServiceClient.verifyPassword(
                request.getEmail(),
                request.getPassword()
        );

        // If invalid password then throw Exception.
        if(!matches) {
            log.warn("Invalid credentials for user: {}", request.getEmail());
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // If MFA is not enabled by user, throw Exception.
        if(!user.getMfaEnabled()) {
            throw new MfaNotEnabledException("MFA setup is not completed");
        }

        // Create the session. Log the session in memory.
        String sessionId = loginSessionService.createSession(user.getEmail());

        log.info("Login initiated for user: {}", user.getEmail());

        // Return the response
        return LoginResponse.builder()
                .loginSessionId(sessionId)
                .mfaRequired(true)
                .build();
    }

    public JwtResponse verifyTotp(
            VerifyTotpRequest request) {
        String email = loginSessionService.getEmail(request.getLoginSessionId());

        UserInfoResponse response = userServiceClient.getUser(email);

        String secret = aesUtil.decrypt(response.getTotpSecretEncrypted());

        Totp totp = new Totp(secret);

        boolean valid = totp.verify(request.getTotp());

        if(!valid) {
            log.warn("Invalid TOTP entered for user: {}", email);
            throw new InvalidOtpException("Invalid TOTP");
        }

        loginSessionService.removeSession(request.getLoginSessionId());

        String token = jwtUtil.generateToken(email);

        log.info("JWT generated for user: {}", email);

        return JwtResponse.builder()
                .accessToken(token)
                .build();
    }
}
