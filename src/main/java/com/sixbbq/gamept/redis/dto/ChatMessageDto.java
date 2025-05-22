package com.sixbbq.gamept.redis.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ChatMessageDto {
    private String message;
    private String sender;
    private String receiver;
    private LocalDateTime timestamp;
}
