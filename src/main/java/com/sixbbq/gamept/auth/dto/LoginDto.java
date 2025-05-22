package com.sixbbq.gamept.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDto {

    private String userId;
    private String password;

    // 기본 생성자
    public LoginDto() {
    }

    // 모든 필드 생성자
    public LoginDto(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginDto{" +
                "userId='" + userId + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}