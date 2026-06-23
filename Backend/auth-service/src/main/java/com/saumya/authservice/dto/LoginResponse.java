package com.saumya.authservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String loginSessionId;
    private Boolean mfaRequired;
}
