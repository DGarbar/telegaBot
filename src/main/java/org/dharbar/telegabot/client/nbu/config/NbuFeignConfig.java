package org.dharbar.telegabot.client.nbu.config;

import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

public class NbuFeignConfig {

    @Bean
    public RequestInterceptor nbuFeignRequestInterceptor() {
        return new NbuFeignInterceptor();
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
