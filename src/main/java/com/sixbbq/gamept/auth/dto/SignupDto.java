package com.sixbbq.gamept.auth.dto;

public class SignupDto {

    private String userId;
    private String password;
    private String passwordConfirm;
    private String nickname;

    // 기본 생성자
    public SignupDto() {
    }

    // 모든 필드 생성자
    public SignupDto(String userId, String password, String passwordConfirm, String nickname) {
        this.userId = userId;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
        this.nickname = nickname;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "SignupDto{" +
                "userId='" + userId + '\'' +
                ", password='[PROTECTED]'" +
                ", passwordConfirm='[PROTECTED]'" +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}