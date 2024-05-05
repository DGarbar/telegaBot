package org.dharbar.telegabot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramConfig {

    @Value("${telegram.name}")
    String botName;
    @Value("${telegram.token}")
    String token;
}

