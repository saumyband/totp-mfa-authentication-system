package com.saumya.authservice.service;

// Multi Factor Authentication

import com.saumya.authservice.client.UserServiceClient;
import com.saumya.authservice.dto.MfaActivationRequest;
import com.saumya.authservice.dto.QrCodeResponse;
import com.saumya.authservice.dto.UserInfoResponse;
import com.saumya.authservice.exception.InvalidOtpException;
import com.saumya.authservice.util.AesUtil;
import lombok.RequiredArgsConstructor;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class MfaService {

    private final UserServiceClient userServiceClient;
    private final AesUtil aesUtil;
    private final QrCodeService qrCodeService;

    public QrCodeResponse setupMfa(String email) throws Exception {
        UserInfoResponse user = userServiceClient.getUser(email);

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

        // Decrypt secret
        String secret = aesUtil.decrypt(
                user.getTotpSecretEncrypted()
        );

        // Verify TOTP using AeroGear. Totp is AeroGear class
        Totp totp = new Totp(secret);
        boolean valid = totp.verify(request.getTotp());

        // If invalid, then return exception. If Valid, then update mfa_enabled to true in user details.
        if(!valid) {
            throw new InvalidOtpException("Invalid OTP");
        } else {
            userServiceClient.enableMfa(request.getEmail());
        }

        return true;
    }
}
