package com.saumya.authservice.client;


import com.saumya.authservice.dto.UserInfoResponse;
import com.saumya.authservice.dto.VerifyPasswordRequest;
import com.saumya.authservice.dto.VerifyPasswordResponse;
import com.saumya.authservice.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class UserServiceClient {

    @Value("${user-service.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public UserInfoResponse getUser(String email) {
        try {
            return restTemplate.getForObject(
                    baseUrl + "user/" + email,
                    UserInfoResponse.class
            );
        } catch (HttpClientErrorException.NotFound ex) {
            throw new UserNotFoundException(
                    "User not found with email: "
                            + email
            );
        }
    }

    public void enableMfa(String email) {
        restTemplate.put(
                baseUrl + "mfa/enable/" + email,
                null                            // null: Send a PUT request with no body
        );

    }

    public boolean verifyPassword(String email, String rawPassword) {
        VerifyPasswordRequest request = new VerifyPasswordRequest();
        request.setPassword(rawPassword);

        VerifyPasswordResponse response = restTemplate.postForObject(
                baseUrl + email + "/verify-password",
                request,
                VerifyPasswordResponse.class
        );

        return response != null && response.isValid();
    }
}
