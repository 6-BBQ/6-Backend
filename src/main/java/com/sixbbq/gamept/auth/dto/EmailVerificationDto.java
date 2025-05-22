package com.sixbbq.gamept.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmailVerificationDto {
    private String email;
    private String verificationCode;
}