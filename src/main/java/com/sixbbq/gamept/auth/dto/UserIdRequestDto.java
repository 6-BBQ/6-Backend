package com.sixbbq.gamept.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserIdRequestDto {
    private String userId;

    // 기본 생성자
    public UserIdRequestDto() {
    }

    // 생성자
    public UserIdRequestDto(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserIdRequestDto{" +
                "userId='" + userId + '\'' +
                '}';
    }
}