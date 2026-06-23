package com.saumya.authservice.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QrCodeResponse {

    private String qrCodeBase64;
}
