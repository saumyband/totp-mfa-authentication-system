package com.saumya.userservice.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RegisterResponse {

    private String email;
    private String message;
}
