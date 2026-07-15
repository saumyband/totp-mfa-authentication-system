package com.saumya.userservice.controller;

import com.saumya.userservice.dto.RegisterRequest;
import com.saumya.userservice.dto.RegisterResponse;
import com.saumya.userservice.dto.UserDetailsResponse;
import com.saumya.userservice.dto.VerifyPasswordRequest;
import com.saumya.userservice.dto.VerifyPasswordResponse;
import com.saumya.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<UserDetailsResponse> getUser(
            @PathVariable String email) {
        return ResponseEntity.ok(
                userService.getUserByEmail(email)
        );
    }

    @PutMapping("/mfa/enable/{email}")
    public ResponseEntity<Void> enableMfa(
            @PathVariable String email) {
        userService.enableMfa(email);

        return ResponseEntity.ok().build();
    }

    // Password hash never leaves user-service — callers only get a yes/no answer.
    @PostMapping("/{email}/verify-password")
    public ResponseEntity<VerifyPasswordResponse> verifyPassword(
            @PathVariable String email,
            @Valid @RequestBody VerifyPasswordRequest request) {
        return ResponseEntity.ok(
                userService.verifyPassword(email, request.getPassword())
        );
    }

}
