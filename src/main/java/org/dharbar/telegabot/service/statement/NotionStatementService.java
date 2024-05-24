package org.dharbar.telegabot.service.statement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.dharbar.telegabot.service.statement.dto.TransactionDto;
import org.dharbar.telegabot.service.statement.mono.MonoService;
import org.dharbar.telegabot.service.statement.notion.NotionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotionStatementService {

    private final NotionService notionService;
    private final MonoService monoService;

    public void updateStatementTable() {
        Pair<LocalDate, List<TransactionDto>> localDateListPair = notionService.lastDayTransaction();
        LocalDate lastNotionTxDate = localDateListPair.getLeft();
        log.info("Last transaction found in {}", lastNotionTxDate);

        List<TransactionDto> transactionFromMono = monoService.getTransactionFromMono(lastNotionTxDate);

        List<TransactionDto> notionTransactions = localDateListPair.getRight();
        List<TransactionDto> newTransactions = transactionFromMono.stream()
                .filter(monoTx -> notionTransactions.stream().noneMatch(notionTx -> isMonoNotionTransactionSame(monoTx, notionTx)))
                .sorted(Comparator.comparing(TransactionDto::getDate))
                .toList();

        notionService.createPages(newTransactions);
    }

    // TODO Bug transaction with same day and sum = equal and not be added second time
    private static boolean isMonoNotionTransactionSame(TransactionDto tx1, TransactionDto tx2) {
        boolean isDateEqual = tx1.getDate().equals(tx2.getDate());
        boolean isSumEqual = Math.abs(tx1.getValue()) == Math.abs(tx2.getValue());
        return isDateEqual && isSumEqual;
    }
}
