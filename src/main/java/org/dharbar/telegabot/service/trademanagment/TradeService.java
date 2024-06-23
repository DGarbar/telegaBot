package org.dharbar.telegabot.service.trademanagment;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.repository.TradeRepository;
import org.dharbar.telegabot.repository.entity.OrderEntity;
import org.dharbar.telegabot.repository.entity.TradeEntity;
import org.dharbar.telegabot.service.trademanagment.dto.TradeDto;
import org.dharbar.telegabot.service.trademanagment.mapper.TradeMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;

    private final TradeMapper tradeMapper;

    public List<TradeDto> getTrades() {
        return tradeRepository.findAll().stream()
                .map(tradeMapper::toDto)
                .toList();
    }

    public void saveTrade(TradeDto tradeDto) {
        Set<OrderEntity> orders = tradeMapper.toOrders(tradeDto.getByuOrder(), tradeDto.getSellOrders());
        TradeEntity trade = tradeMapper.toEntity(tradeDto, orders);
        tradeRepository.save(trade);
    }
}
