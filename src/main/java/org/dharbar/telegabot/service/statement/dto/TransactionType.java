package org.dharbar.telegabot.service.statement.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Getter
public enum TransactionType {
    // 6536, 6538 7399 8999 оплата всякого НовойПочтой
    // 5310 ALI

    FOOD("Food", Set.of(4215, 5411, 5441, 5451, 5462, 5499, 5814, 5812)),
    HOME("Home", Set.of(4111, 4814, 4816, 4900, 5310, 5399, 5661, 5699, 5977, 5949)),
    ENTERTAINMENT("Entertainment", Set.of(4215, 5441, 5812, 5814)),
    TECH("Tech", Set.of(5722)),
    HEALTH("Health", Set.of(5912, 8099, 7997, 8062)),
    OTHER("Other", Set.of(4121));

    //  4829 = карточные переводы которые нужно игнорить или продумать как с ними

    private final String notionName;
    private final Set<Integer> mcc;

    public static List<TransactionType> fromMcc(int code) {
        return Arrays.stream(values())
                .filter(transactionType -> transactionType.getMcc().contains(code))
                .toList();
    }
}
