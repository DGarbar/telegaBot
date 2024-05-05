package org.dharbar.telegabot.client.notion.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.dharbar.telegabot.client.notion.dto.TableProperties;

import java.util.List;

@Data
public class NotionPageResponse {
    private List<Result> results;

    @Data
    public static class Result {
        @JsonProperty("in_trash")
        private Boolean inTrash;

        private TableProperties properties;
    }

}

