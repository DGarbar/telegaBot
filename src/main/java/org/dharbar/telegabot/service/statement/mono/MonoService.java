package org.dharbar.telegabot.service.statement.mono;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.client.mono.MonoClient;
import org.dharbar.telegabot.client.mono.response.TransactionMonoResponse;
import org.dharbar.telegabot.service.statement.dto.TransactionDto;
import org.dharbar.telegabot.service.statement.mapper.TransactionMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MonoService {

    private static final Period ADJUSMENT = Period.ofDays(10);

    private final MonoClient monoApi;
    private final TransactionMapper transactionMapper;

    // No more than 31 day + 1 hour
    // 1 request in minute
    // No more than 500 txs
    public List<TransactionDto> getTransactionFromMono(LocalDate from) {
        LocalDate to = from.plus(ADJUSMENT);

        long fromEpoch = from.atStartOfDay().toEpochSecond(ZoneOffset.UTC);
        long toEpoch = to.atStartOfDay().toEpochSecond(ZoneOffset.UTC);
        return monoApi.getTransactions(fromEpoch, toEpoch).stream()
                .filter(MonoService::isDebitTransaction)
                .sorted(Comparator.comparing(TransactionMonoResponse::getTime))
                .map(transactionMapper::toDto)
                .toList();
    }

    private static boolean isDebitTransaction(TransactionMonoResponse tx) {
        return tx.getAmount() < 0;
    }
}
