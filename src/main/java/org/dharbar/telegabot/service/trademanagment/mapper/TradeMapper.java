package org.dharbar.telegabot.service.trademanagment.mapper;

import org.dharbar.telegabot.repository.entity.OrderEntity;
import org.dharbar.telegabot.repository.entity.OrderType;
import org.dharbar.telegabot.repository.entity.TradeEntity;
import org.dharbar.telegabot.service.trademanagment.dto.OrderDto;
import org.dharbar.telegabot.service.trademanagment.dto.TradeDto;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Mapper(componentModel = "spring", collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED, imports = {OrderType.class})
public interface TradeMapper {

    @Mapping(target = "byuOrder", expression = "java(findOrder(tradeEntity.getOrders(), OrderType.BUY))")
    @Mapping(target = "sellOrder", expression = "java(findOrder(tradeEntity.getOrders(), OrderType.SELL))")
    TradeDto toDto(TradeEntity tradeEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profitPercentage", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "netProfitUsd", ignore = true)
    @Mapping(target = "isClosed", constant = "false")
    TradeEntity toNewEntity(OrderEntity orderDto);

    OrderDto toDto(OrderEntity orderEntity);

    @Mapping(target = "tradeId", ignore = true)
    OrderEntity toEntity(OrderDto orders);

    Set<OrderEntity> toEntities(Set<OrderDto> orders);

    default OrderDto findOrder(Collection<OrderEntity> orders, OrderType type) {
        return orders.stream()
                .filter(order -> type == order.getType())
                .findFirst()
                .map(this::toDto)
                .orElse(null);
    }

    default Set<OrderEntity> toOrders(OrderDto buyOrder, Set<OrderDto> orders) {
        Set<OrderEntity> allOrders = new HashSet<>();
        allOrders.add(toEntity(buyOrder));
        allOrders.addAll(toEntities(orders));
        return allOrders;
    }
}
