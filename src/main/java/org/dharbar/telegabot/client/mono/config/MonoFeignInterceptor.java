package org.dharbar.telegabot.client.mono.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MonoFeignInterceptor implements RequestInterceptor {

    private final String token;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("X-Token", token);
    }
}
