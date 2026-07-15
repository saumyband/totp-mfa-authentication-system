package com.saumya.authservice.service;

// Multi Factor Authentication

import com.saumya.authservice.client.UserServiceClient;
import com.saumya.authservice.dto.MfaActivationRequest;
import com.saumya.authservice.dto.QrCodeResponse;
import com.saumya.authservice.dto.UserInfoResponse;
import com.saumya.authservice.exception.InvalidOtpException;
import com.saumya.authservice.exception.MfaAlreadyEnabledException;
import com.saumya.authservice.util.AesUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MfaService {

    private final UserServiceClient userServiceClient;
    private final AesUtil aesUtil;
    private final QrCodeService qrCodeService;

    public QrCodeResponse setupMfa(String email) throws Exception {
        UserInfoResponse user = userServiceClient.getUser(email);

        // Same replay concern as activateMfa: once MFA is enabled, this endpoint
        // must stop handing back the live secret to anyone who knows the email.
        if (Boolean.TRUE.equals(user.getMfaEnabled())) {
            log.warn("Rejected MFA setup QR request on already-enabled account: {}", email);
            throw new MfaAlreadyEnabledException("MFA is already enabled for this account");
        }

        log.info("MFA setup QR requested for user: {}", email);

        // Decrypt actual TOTP secret
        String secret = aesUtil.decrypt(user.getTotpSecretEncrypted());

        // Generate OTP-Auth URI
        String otpAuthUrl = qrCodeService.generateOtpAuthUrl(email, secret);

        // Generate QR with otpAuth URL using Base64. Why? Becoz REST APIs cannot directly send images easily
        String qr = qrCodeService.generateQrCode(otpAuthUrl);

        return QrCodeResponse.builder()
                .qrCodeBase64(qr)
                .build();
    }

    public boolean activateMfa(MfaActivationRequest request) {
        UserInfoResponse user = userServiceClient.getUser(request.getEmail());

        // Once MFA is enabled, this endpoint must not be usable again — otherwise
        // anyone who still knows/guesses the email could keep probing it.
        if (Boolean.TRUE.equals(user.getMfaEnabled())) {
            log.warn("Rejected MFA activation attempt on already-enabled account: {}", request.getEmail());
            throw new MfaAlreadyEnabledException("MFA is already enabled for this account");
        }

        // Decrypt secret
        String secret = aesUtil.decrypt(
                user.getTotpSecretEncrypted()
        );

        // Verify TOTP using AeroGear. Totp is AeroGear class
        Totp totp = new Totp(secret);
        boolean valid = totp.verify(request.getTotp());

        // If invalid, then return exception. If Valid, then update mfa_enabled to true in user details.
        if(!valid) {
            log.warn("Invalid TOTP entered while activating MFA for user: {}", request.getEmail());
            throw new InvalidOtpException("Invalid OTP");
        } else {
            userServiceClient.enableMfa(request.getEmail());
            log.info("MFA activated for user: {}", request.getEmail());
        }

        return true;
    }
}
