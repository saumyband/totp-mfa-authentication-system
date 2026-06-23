package com.saumya.authservice.controller;

import com.saumya.authservice.dto.*;
import com.saumya.authservice.service.AuthService;
import com.saumya.authservice.service.MfaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final MfaService mfaService;
    private final AuthService authService;

    // For setup of MFA
    @GetMapping("/mfa/setup/{email}")
    public ResponseEntity<QrCodeResponse> setupMfa(
            @PathVariable String email) throws Exception {
        return ResponseEntity.ok(mfaService.setupMfa(email));
    }

    // For activating the MFA. Used in case user has just created an account but haven't activated MFA.
    @PostMapping("/mfa/activate")
    public ResponseEntity<Boolean> activateMfa(
            @Valid @RequestBody MfaActivationRequest request) {
        return ResponseEntity.ok(mfaService.activateMfa(request));
    }

    // Login API
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // Verify TOTP
    @PostMapping("/verify-totp")
    public ResponseEntity<JwtResponse> verifyTotp(
            @Valid @RequestBody VerifyTotpRequest request) {
        return ResponseEntity.ok(authService.verifyTotp(request));
    }
}
