package org.dharbar.telegabot.client.binance.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BinanceFeignInterceptor implements RequestInterceptor {

    private final String key;
    private final String secret;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("binance-api-key", key);
        requestTemplate.header("binance-api-secret", secret);
    }
}
