package com.saumya.authservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class ProfileController {

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> profile(
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        response.put(
                "email",
                authentication.getName()
        );

        response.put(
                "message",
                "Protected API accessed successfully"
        );

        log.info("Protected endpoint accessed by user: {}", authentication.getName());

        return ResponseEntity.ok(response);
    }
}
