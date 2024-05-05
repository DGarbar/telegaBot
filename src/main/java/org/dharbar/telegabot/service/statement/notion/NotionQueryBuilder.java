package org.dharbar.telegabot.service.statement.notion;

import org.dharbar.telegabot.client.notion.NotionClient;
import org.dharbar.telegabot.client.notion.dto.TableProperties;
import org.dharbar.telegabot.client.notion.dto.TableProperties.Property;
import org.dharbar.telegabot.client.notion.request.CreatePageRequest;
import org.dharbar.telegabot.client.notion.request.QueryRequest;
import org.dharbar.telegabot.client.notion.request.QueryRequest.Filter.FilterNode.PropertyFilterCriteria;
import org.dharbar.telegabot.service.statement.dto.TransactionDto;
import org.dharbar.telegabot.service.statement.dto.TransactionType;

import java.time.LocalDate;
import java.util.List;

import static org.dharbar.telegabot.client.notion.dto.TableProperties.Property.DateValue;
import static org.dharbar.telegabot.client.notion.dto.TableProperties.Property.MultiSelect;
import static org.dharbar.telegabot.client.notion.dto.TableProperties.Property.RichText;
import static org.dharbar.telegabot.client.notion.dto.TableProperties.Property.RichText.Text;
import static org.dharbar.telegabot.client.notion.request.QueryRequest.Filter;
import static org.dharbar.telegabot.client.notion.request.QueryRequest.Filter.FilterNode;

public class NotionQueryBuilder {

    public static final QueryRequest LAST_TRANSACTION_QUERY = QueryRequest.builder()
            .sorts(List.of(new QueryRequest.Sort("Date", "descending")))
            .pageSize(1)
            .build();

    public static QueryRequest transactionByDayQuery(LocalDate localDate) {
        FilterNode dateEqCriteria = new FilterNode(
                "Date",
                PropertyFilterCriteria.builder().equals(localDate.toString()).build());

        return QueryRequest.builder()
                .sorts(List.of(new QueryRequest.Sort("Date", "descending")))
                .filter(Filter.builder()
                        .and(List.of(dateEqCriteria))
                        .build())
                .build();
    }

    public static CreatePageRequest createPageRequest(TransactionDto transactionDto) {
        List<MultiSelect> tags = transactionDto.getTypes().stream()
                .map(TransactionType::getNotionName)
                .map(MultiSelect::of)
                .toList();

        LocalDate date = transactionDto.getDate();
        DateValue dateValue = DateValue.builder().start(date).build();

        double amount = Math.abs(transactionDto.getValue());

        TableProperties tableProperties = TableProperties.builder()
                .amount(Property.builder().number(amount).build())
                .category(Property.builder().multiSelect(tags).build())
                .date(Property.builder().date(dateValue).build())
                .comment(toCommentProperty(transactionDto.getComment()))
                .build();
        return CreatePageRequest.builder()
                .parent(CreatePageRequest.Parent.builder().databaseId(NotionClient.TRANSACTION_DATABASE_ID).build())
                .properties(tableProperties)
                .build();
    }

    private static Property toCommentProperty(String comment) {
        if (comment == null) {
            return null;
        }
        RichText richText = RichText.of(Text.of(comment));
        return Property.builder().richText(List.of(richText)).build();
    }

}
