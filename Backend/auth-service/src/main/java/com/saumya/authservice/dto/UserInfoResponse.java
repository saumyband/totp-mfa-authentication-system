package com.saumya.authservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {

    private Long id;
    private String email;
    private String passwordHash;
    private Boolean mfaEnabled;
    private String totpSecretEncrypted;
}
