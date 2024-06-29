package org.dharbar.telegabot.client.ttingo.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TiingoFeignInterceptor implements RequestInterceptor {

    private final String token;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("Authorization", "Token " + token);
        requestTemplate.header("Content-Type", "application/json");
    }
}
