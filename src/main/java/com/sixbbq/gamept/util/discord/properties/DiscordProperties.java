package com.sixbbq.gamept.util.discord.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DiscordProperties {
    @Value("${discord.url}")
    private String discordUrl;

    @Value("${discord.admin-discord-id}")
    private String adminId;

    public String getDiscordUrl() {
        return discordUrl;
    }

    public String getAdminId() {
        return adminId;
    }
}
