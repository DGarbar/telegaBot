package org.dharbar.telegabot.service.trademanagment;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.repository.TradeRepository;
import org.dharbar.telegabot.repository.entity.OrderEntity;
import org.dharbar.telegabot.repository.entity.TradeEntity;
import org.dharbar.telegabot.service.trademanagment.dto.OrderDto;
import org.dharbar.telegabot.service.trademanagment.dto.TradeDto;
import org.dharbar.telegabot.service.trademanagment.mapper.TradeMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;

    private final TradeMapper tradeMapper;

    public TradeDto getTrade(UUID tradeId) {
        return tradeRepository.findById(tradeId)
                .map(tradeMapper::toDto)
                .orElseThrow();
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

    public TradeDto saveNewTrade(OrderDto orderDto) {
        OrderEntity order = tradeMapper.toEntity(orderDto);
        TradeEntity newTrade = tradeMapper.toNewEntity(order);
        // TODO fix with mapper
        newTrade.addOrder(order);

        TradeEntity savedTrade = tradeRepository.save(newTrade);
        return tradeMapper.toDto(savedTrade);
    }

    public TradeDto saveTrade(TradeDto tradeDto) {
        Set<OrderEntity> orders = tradeMapper.toEntities(tradeDto.getOrders());
        TradeEntity trade = tradeMapper.toEntity(tradeDto, orders);
        TradeEntity savedTrade = tradeRepository.save(trade);

        return tradeMapper.toDto(savedTrade);
    }
}
