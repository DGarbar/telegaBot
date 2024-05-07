package org.dharbar.telegabot.client.mono.config;

import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

public class MonoFeignConfig {

    @Bean
    public RequestInterceptor monoFeignRequestInterceptor(@Value("${api.mono.token}") String monoToken) {
        return new MonoFeignInterceptor(monoToken);
    }

    @Profile("dev")
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
