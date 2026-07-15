package com.saumya.authservice.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@Component
public class AesUtil {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH_BYTES = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;

    // Base64-encoded 16/24/32-byte key. Generate with: openssl rand -base64 32
    // Must match the AES_SECRET_KEY used by user-service — one encrypts, the other decrypts.
    @Value("${aes.secret-key}")
    private String secretKey;

    private final SecureRandom secureRandom = new SecureRandom();

    public String encrypt(String data) {
        try {
            SecretKeySpec key = new SecretKeySpec(Base64.getDecoder().decode(secretKey), "AES");

            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));

            byte[] encrypted = cipher.doFinal(data.getBytes());

            byte[] ivAndCiphertext = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, ivAndCiphertext, 0, iv.length);
            System.arraycopy(encrypted, 0, ivAndCiphertext, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(ivAndCiphertext);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public String decrypt(String encryptedData) {
        try {
            SecretKeySpec key = new SecretKeySpec(Base64.getDecoder().decode(secretKey), "AES");

            byte[] ivAndCiphertext = Base64.getDecoder().decode(encryptedData);

            byte[] iv = Arrays.copyOfRange(ivAndCiphertext, 0, GCM_IV_LENGTH_BYTES);
            byte[] ciphertext = Arrays.copyOfRange(ivAndCiphertext, GCM_IV_LENGTH_BYTES, ivAndCiphertext.length);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));

            return new String(cipher.doFinal(ciphertext));
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}
