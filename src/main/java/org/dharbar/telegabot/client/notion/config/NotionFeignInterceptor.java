package org.dharbar.telegabot.client.notion.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotionFeignInterceptor implements RequestInterceptor {

    private final String token;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("Notion-Version", "2022-02-22");
        requestTemplate.header("Authorization", "Bearer " + token);
    }
}
