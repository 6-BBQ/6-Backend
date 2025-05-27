package com.sixbbq.gamept.util.discord.properties;

import com.sixbbq.gamept.util.ErrorUtil;
import org.springframework.stereotype.Component;

@Component
public class DiscordInitializer {

    public DiscordInitializer(DiscordProperties discordProperties) {
        ErrorUtil.setDiscordProperties(discordProperties);
    }
}