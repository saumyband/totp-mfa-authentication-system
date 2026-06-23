package com.saumya.userservice.service;

import org.apache.commons.codec.binary.Base32;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class TotpSecretService {

    // final=create once rather than creating object every time
    // Creates a Base32 encoder. Why? Auth apps expect secrets in Base32 format.
    private static final Base32 BASE32 = new Base32();

    public String generateSecret() {
        SecureRandom secureRandom = new SecureRandom(); // Creates a cryptographically secure random number generator

        // Contains secret, unpredictable values
        byte[] bytes = new byte[20];                 // Creates an array of 20 bytes. Each byte = 8 bits. So, 20*8=160 bits.

        secureRandom.nextBytes(bytes);              // Fills the array with random values

        return BASE32.encodeToString(bytes);       // Converts the random bytes into a Base32 string. Becomes the user secret key
    }
}
