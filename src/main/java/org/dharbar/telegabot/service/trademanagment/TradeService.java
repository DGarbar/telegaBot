package org.dharbar.telegabot.service.trademanagment;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.repository.TradeRepository;
import org.dharbar.telegabot.repository.entity.OrderEntity;
import org.dharbar.telegabot.repository.entity.OrderType;
import org.dharbar.telegabot.repository.entity.TradeEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;

    @Transactional
    public void createTestTrade() {
        TradeEntity trade = TradeEntity.builder()
                .netProfitUsd(BigDecimal.ONE)
                .profitPercentage(BigDecimal.ZERO)
                .build();

        OrderEntity order = OrderEntity.builder()
                .id(UUID.randomUUID())
                .type(OrderType.BUY)
                .ticker("AAPL")
                .quantity(10.0)
                .dateAt(LocalDate.now())
                .rate(BigDecimal.TEN)
                .totalUsd(BigDecimal.valueOf(1000.0))
                .commissionUsd(BigDecimal.valueOf(1.0))
                .build();

        trade.addOrder(order);

        tradeRepository.save(trade);
    }
}
