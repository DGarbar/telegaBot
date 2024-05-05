package org.dharbar.telegabot.client.notion;

import org.dharbar.telegabot.client.notion.config.NotionFeignConfig;
import org.dharbar.telegabot.client.notion.request.CreatePageRequest;
import org.dharbar.telegabot.client.notion.request.QueryRequest;
import org.dharbar.telegabot.client.notion.response.NotionPageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "notion-client", url = "${api.notion.url}", configuration = NotionFeignConfig.class)
public interface NotionClient {

    String TRANSACTION_DATABASE_ID = "ad315adf365f4e24aa812692263d401f";
    CreatePageRequest.Parent PARENT = CreatePageRequest.Parent.builder().databaseId(TRANSACTION_DATABASE_ID).build();

    @PostMapping(value = "/databases/" + TRANSACTION_DATABASE_ID + "/query")
    NotionPageResponse getTransactions(QueryRequest queryRequest);

    @PostMapping(value = "/pages")
    void addPage(CreatePageRequest request);
}
