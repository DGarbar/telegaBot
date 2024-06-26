package org.dharbar.telegabot.service.trademanagment;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.repository.TradeRepository;
import org.dharbar.telegabot.repository.entity.OrderEntity;
import org.dharbar.telegabot.repository.entity.OrderType;
import org.dharbar.telegabot.repository.entity.TradeEntity;
import org.dharbar.telegabot.service.trademanagment.dto.OrderDto;
import org.dharbar.telegabot.service.trademanagment.dto.TradeDto;
import org.dharbar.telegabot.service.trademanagment.mapper.TradeMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;

    private final TradeMapper tradeMapper;

    public Set<String> getTickers() {
        return tradeRepository.findDistinctTicker();
    }

    public List<TradeDto> getOpenTrades(PageRequest pageRequest) {
        return tradeRepository.findAllByIsClosedIsFalse(pageRequest).stream()
                .map(tradeMapper::toDto)
                .toList();
    }

    public List<TradeDto> getTrades(PageRequest pageRequest) {
        return tradeRepository.findAll(pageRequest).stream()
                .map(tradeMapper::toDto)
                .toList();
    }

    public void saveNewTrade(OrderDto orderDto) {
        OrderEntity order = tradeMapper.toEntity(orderDto);
        TradeEntity newTrade = tradeMapper.toNewEntity(order);
        // TODO fix with mapper
        newTrade.addOrder(order);

        tradeRepository.save(newTrade);
    }

    // TODO (later) for update for trade
    @Transactional
    public void saveTradeSellOrder(UUID tradeId, OrderDto sellOrderDto) {
        OrderEntity order = tradeMapper.toEntity(sellOrderDto);
        TradeEntity trade = tradeRepository.findById(tradeId).orElseThrow();
        trade.addOrder(order);

        BigDecimal buyTotal = BigDecimal.ZERO;
        BigDecimal buyQuantity = BigDecimal.ZERO;
        BigDecimal sellTotal = BigDecimal.ZERO;
        BigDecimal sellQuantity = BigDecimal.ZERO;
        BigDecimal commissionTotal = BigDecimal.ZERO;
        for (OrderEntity tradeOrder : trade.getOrders()) {
            if (OrderType.BUY == tradeOrder.getType()) {
                buyTotal = buyTotal.add(tradeOrder.getTotalUsd());
                buyQuantity = buyQuantity.add(tradeOrder.getQuantity());
            } else {
                sellTotal = sellTotal.add(tradeOrder.getTotalUsd());
                sellQuantity = sellQuantity.add(tradeOrder.getQuantity());
            }
            commissionTotal = commissionTotal.add(tradeOrder.getCommissionUsd());
        }

        BigDecimal netProfit = sellTotal.subtract(buyTotal).subtract(commissionTotal);
        trade.setNetProfitUsd(netProfit);
        trade.setProfitPercentage(netProfit.divide(buyTotal, 3, RoundingMode.HALF_UP));
        trade.setIsClosed(buyQuantity.compareTo(sellQuantity) == 0);

        tradeRepository.save(trade);
    }
}
