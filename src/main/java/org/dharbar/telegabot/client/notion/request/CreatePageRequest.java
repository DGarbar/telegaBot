package org.dharbar.telegabot.client.notion.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import org.dharbar.telegabot.client.notion.dto.TableProperties;

@Builder
@Value
public class CreatePageRequest {
    Parent parent;
    TableProperties properties;

    @Builder
    @Value
    public static class Parent {
        @JsonProperty("database_id")
        String databaseId;
    }
}
