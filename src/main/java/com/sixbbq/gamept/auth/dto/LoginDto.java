package com.sixbbq.gamept.auth.dto;

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

    @Override
    public String toString() {
        return "LoginDto{" +
                "userId='" + userId + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}