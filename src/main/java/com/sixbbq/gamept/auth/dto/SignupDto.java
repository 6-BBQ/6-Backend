package com.sixbbq.gamept.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupDto {

    private String userId;
    private String email;
    private String password;
    private String passwordConfirm;
    private String nickname;

    // 기본 생성자
    public SignupDto() {
    }

    // 모든 필드 생성자
    public SignupDto(String userId, String email, String password, String passwordConfirm, String nickname) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "SignupDto{" +
                "userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                ", passwordConfirm='[PROTECTED]'" +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}