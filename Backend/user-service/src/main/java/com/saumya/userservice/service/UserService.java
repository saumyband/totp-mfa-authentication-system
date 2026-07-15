package com.saumya.userservice.service;

import com.saumya.userservice.dto.RegisterRequest;
import com.saumya.userservice.dto.RegisterResponse;
import com.saumya.userservice.dto.UserDetailsResponse;
import com.saumya.userservice.dto.VerifyPasswordResponse;
import com.saumya.userservice.entity.User;
import com.saumya.userservice.exception.UserAlreadyExistsException;
import com.saumya.userservice.exception.UserNotFoundException;
import com.saumya.userservice.repository.UserRepository;
import com.saumya.userservice.util.AesUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TotpSecretService totpSecretService;
    private final AesUtil aesUtil;

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed. Email already exists: {}", request.getEmail());
            throw new UserAlreadyExistsException("User already exists with email: " + request.getEmail());
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        String secret = totpSecretService.generateSecret();
        String encryptedSecret = aesUtil.encrypt(secret);

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(hashedPassword)
                .mfaEnabled(false)
                .totpSecretEncrypted(encryptedSecret)
                .build();

        User savedUser = userRepository.save(user);

        log.info("User registered successfully: {}", request.getEmail());

        return RegisterResponse.builder()
                .message("User registered successfully")
                .email(savedUser.getEmail())
                .build();
    }

    public UserDetailsResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        return UserDetailsResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .mfaEnabled(user.getMfaEnabled())
                .totpSecretEncrypted(user.getTotpSecretEncrypted())
                .build();
    }

    public VerifyPasswordResponse verifyPassword(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        boolean valid = passwordEncoder.matches(rawPassword, user.getPasswordHash());

        return VerifyPasswordResponse.builder()
                .valid(valid)
                .build();
    }

    public void enableMfa(String email) {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        user.setMfaEnabled(true);

        userRepository.save(user);

        log.info("MFA enabled for user: {}", email);
    }
}
