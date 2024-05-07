package org.dharbar.telegabot.client.binance.config;

import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

public class BinanceFeignConfig {

    @Bean
    public RequestInterceptor binanceFeignRequestInterceptor(@Value("${api.binance.key}") String binanceKey,
                                                             @Value("${api.binance.secret}") String binanceSecret) {
        return new BinanceFeignInterceptor(binanceKey, binanceSecret);
    }

    @Profile("dev")
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
