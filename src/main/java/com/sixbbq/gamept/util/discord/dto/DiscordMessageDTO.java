package com.sixbbq.gamept.util.discord.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class DiscordMessageDTO {
    private String username;
    private String content;
    private boolean tts = false;
    private List<Embed> embeds;

    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    @Builder
    public static class Embed {
        private String title;
        private String description;
    }
}
