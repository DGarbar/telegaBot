package org.dharbar.telegabot.client.notion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableProperties {
    // Could be map<String, Property>
    @JsonProperty("Amount")
    private Property amount;
    @JsonProperty("Category")
    private Property category;
    @JsonProperty("Date")
    private Property date;
    @JsonProperty("Comment")
    private Property comment;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Property {
        // for amount
        private Double number;

        // for category
        @JsonProperty("multi_select")
        private List<MultiSelect> multiSelect;

        // for date
        private DateValue date;

        @JsonProperty("rich_text")
        private List<RichText> richText;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor(staticName = "of")
        @Builder
        public static class MultiSelect {
            String name;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class DateValue {

            LocalDate start;
            LocalDate end;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor(staticName = "of")
        @Builder
        public static class RichText {

            Text text;

            @Data
            @NoArgsConstructor
            @AllArgsConstructor(staticName = "of")
            @Builder
            public static class Text {

                String content;
            }
        }
    }

}
