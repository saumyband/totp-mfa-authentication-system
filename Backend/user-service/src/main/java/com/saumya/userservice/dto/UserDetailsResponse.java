package com.saumya.userservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsResponse {

    private Long id;
    private String email;
    private String passwordHash;
    private Boolean mfaEnabled;
    private String totpSecretEncrypted;
}
