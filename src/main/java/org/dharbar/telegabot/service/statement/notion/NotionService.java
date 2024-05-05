package org.dharbar.telegabot.service.statement.notion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.dharbar.telegabot.client.notion.NotionClient;
import org.dharbar.telegabot.client.notion.request.CreatePageRequest;
import org.dharbar.telegabot.client.notion.response.NotionPageResponse;
import org.dharbar.telegabot.service.statement.dto.TransactionDto;
import org.dharbar.telegabot.service.statement.mapper.TransactionMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotionService {

    private final NotionClient notionApi;
    private final TransactionMapper transactionMapper;

    public void createPages(List<TransactionDto> transactions) {
        for (TransactionDto transaction : transactions) {
            log.info("Creating tx with date {} and amount {}", transaction.getDate(), transaction.getValue());
            CreatePageRequest pageCreationRequest = NotionQueryBuilder.createPageRequest(transaction);
            notionApi.addPage(pageCreationRequest);
        }
    }

    public Pair<LocalDate, List<TransactionDto>> lastDayTransaction() {
        LocalDate lastTxDate = getLastTransaction().getDate();
        return Pair.of(lastTxDate, getTransactionsPerDay(lastTxDate));
    }

    private List<TransactionDto> getTransactionsPerDay(LocalDate date) {
        NotionPageResponse pageResponse = notionApi.getTransactions(NotionQueryBuilder.transactionByDayQuery(date));
        return toTransactionDtos(pageResponse);
    }

    private TransactionDto getLastTransaction() {
        NotionPageResponse pageResponse = notionApi.getTransactions(NotionQueryBuilder.LAST_TRANSACTION_QUERY);
        return toTransactionDtos(pageResponse).get(0);
    }

    private List<TransactionDto> toTransactionDtos(NotionPageResponse pageResponse) {
        return pageResponse.getResults().stream()
                .map(NotionPageResponse.Result::getProperties)
                .map(transactionMapper::toDto)
                .toList();
    }
}
