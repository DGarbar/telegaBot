package org.dharbar.telegabot.client.notion.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Builder
@Value
public class QueryRequest {
    List<Sort> sorts;
    Filter filter;
    @JsonProperty("page_size")
    Integer pageSize;

    @Value
    public static class Sort {
        String property;
        String direction;
    }

    @Value
    @Builder
    public static class Filter {
        List<FilterNode> and;

        @Value
        public static class FilterNode {
            @JsonProperty("property")
            String propertyName;
            PropertyFilterCriteria date;

            @Builder
            @Value
            public static class PropertyFilterCriteria {
                @JsonProperty("on_or_after")
                String onOrAfter;
                String before;
                String equals;
            }
        }
    }
}
