package com.saumya.authservice.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
public class QrCodeService {

    // Create otp-auth URI
    public String generateOtpAuthUrl(String email, String secret) {
        // Starting with otpauth because Authenticator apps follow the format
        return String.format(
                "otpauth://totp/TOTP-Project-by-Saumya:%s?secret=%s&issuer=TOTP-Project-by-Saumya",
                email,
                secret
        );
    }

    // Generate QR image
    public String generateQrCode(String text) throws Exception {
        // Creating QR Matrix
        BitMatrix matrix = new MultiFormatWriter().encode(
                text,
                BarcodeFormat.QR_CODE,
                300,
                300
        );

        // Converting QR Matrix to image
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(
                matrix,
                "PNG",
                stream
        );

        // Convert to Base64
        return Base64.getEncoder()
                .encodeToString(stream.toByteArray());
    }
}
