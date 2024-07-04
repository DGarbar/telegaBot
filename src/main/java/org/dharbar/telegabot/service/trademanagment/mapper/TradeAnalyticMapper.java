package org.dharbar.telegabot.service.trademanagment.mapper;

import org.dharbar.telegabot.repository.entity.OrderType;
import org.dharbar.telegabot.service.trademanagment.dto.OrderDto;
import org.dharbar.telegabot.service.trademanagment.dto.TradeAnalyticDto;
import org.dharbar.telegabot.service.trademanagment.dto.TradeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;

@Mapper(componentModel = "spring", imports = {OrderType.class})
public interface TradeAnalyticMapper {

    @Mapping(target = "byuOrder", expression = "java(findOrder(tradeDto.getOrders(), OrderType.BUY))")
    @Mapping(target = "sellOrder", expression = "java(findOrder(tradeDto.getOrders(), OrderType.SELL))")
    TradeAnalyticDto toDto(TradeDto tradeDto);

    default OrderDto findOrder(Collection<OrderDto> orders, OrderType type) {
        return orders.stream()
                .filter(order -> type == order.getType())
                .findFirst()
                .orElse(null);
    }
}
