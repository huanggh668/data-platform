package com.dataplatform.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RefreshTokenRequest {
    @NotBlank(message = "Refresh token cannot be blank")
    private String refreshToken;
}
