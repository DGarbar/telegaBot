package org.dharbar.telegabot.client.nbu.config;

import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

public class NbuFeignConfig {

    @Bean
    public RequestInterceptor nbuFeignRequestInterceptor() {
        return new NbuFeignInterceptor();
    }

    @Profile("dev")
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
