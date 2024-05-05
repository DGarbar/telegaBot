package org.dharbar.telegabot.service.statement.mapper;

import org.apache.commons.lang3.StringUtils;
import org.dharbar.telegabot.client.mono.response.TransactionMonoResponse;
import org.dharbar.telegabot.client.notion.dto.TableProperties;
import org.dharbar.telegabot.service.statement.dto.TransactionDto;
import org.dharbar.telegabot.service.statement.dto.TransactionType;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class TransactionMapper {

    public TransactionDto toDto(TableProperties properties) {
        List<TransactionType> types = properties.getCategory().getMultiSelect().stream()
                .map(TableProperties.Property.MultiSelect::getName)
                .map(notionCategory -> TransactionType.valueOf(StringUtils.upperCase(notionCategory)))
                .toList();
        return TransactionDto.builder()
                .value(properties.getAmount().getNumber())
                .types(types)
                .date(properties.getDate().getDate().getStart())
                .build();
    }

    public TransactionDto toDto(TransactionMonoResponse response) {
        List<TransactionType> types = getTransactionTypes(response);
        LocalDate date = Instant.ofEpochSecond(response.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        return TransactionDto.builder()
                .value((double) response.getAmount() / 100)
                .types(types)
                .comment(toEmptyMccComment(response))
                .date(date)
                .build();
    }

    private static List<TransactionType> getTransactionTypes(TransactionMonoResponse response) {
        int mcc = response.getMcc();
        List<TransactionType> types = TransactionType.fromMcc(mcc);
        if (types.isEmpty()) {

            boolean isCardTransfer = mcc == 4829;
            if (isCardTransfer && "вода ломоносова".equals(response.getDescription())) {
                return List.of(TransactionType.FOOD);
            }
        }
        return types;
    }

    private static String toEmptyMccComment(TransactionMonoResponse response) {
        return response.getDescription() + " " + response.getMcc();
    }

}
