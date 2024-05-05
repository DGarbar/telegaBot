package org.dharbar.telegabot.client.mono.config;

import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class MonoFeignConfig {

    @Bean
    public RequestInterceptor monoFeignRequestInterceptor(@Value("${api.mono.token}") String monoToken) {
        return new MonoFeignInterceptor(monoToken);
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
