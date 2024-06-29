package org.dharbar.telegabot.client.ttingo.config;

import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

public class TiingoFeignConfig {

    @Bean
    public RequestInterceptor notionFeignRequestInterceptor(@Value("${api.tiingo.token}") String tiingoToken) {
        return new TiingoFeignInterceptor(tiingoToken);
    }

    @Profile("dev")
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
