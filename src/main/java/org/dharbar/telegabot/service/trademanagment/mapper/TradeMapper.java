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
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface TradeMapper {

    @Mapping(target = "byuOrder", expression = "java(findBuyOrder(tradeEntity.getOrders()))")
    @Mapping(target = "sellOrders", expression = "java(findSellOrders(tradeEntity.getOrders()))")
    TradeDto toDto(TradeEntity tradeEntity);

    OrderDto toDto(OrderEntity orderEntity);

    TradeEntity toEntity(TradeDto tradeDto, Set<OrderEntity> orders);

    @Mapping(target="tradeId", ignore = true)
    OrderEntity toEntity(OrderDto orders);

    Set<OrderEntity> toEntities(Set<OrderDto> orders);

    default OrderDto findBuyOrder(Collection<OrderEntity> orders) {
        return orders.stream()
                .filter(order -> OrderType.BUY.equals(order.getType()))
                .findFirst()
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Buy order not found"));
    }

    default Set<OrderDto> findSellOrders(Collection<OrderEntity> orders) {
        return orders.stream()
                .filter(order -> OrderType.SELL.equals(order.getType()))
                .map(this::toDto)
                .collect(Collectors.toSet());
    }

    default Set<OrderEntity> toOrders(OrderDto buyOrder, Set<OrderDto> orders) {
        Set<OrderEntity> allOrders = new HashSet<>();
        allOrders.add(toEntity(buyOrder));
        allOrders.addAll(toEntities(orders));
        return allOrders;
    }
}
