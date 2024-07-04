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
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED, imports = {OrderType.class})
public interface TradeMapper {

    TradeDto toDto(TradeEntity tradeEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profitPercentage", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "netProfitUsd", ignore = true)
    @Mapping(target = "isClosed", constant = "false")
    TradeEntity toNewEntity(OrderEntity orderDto);

    TradeEntity toEntity(TradeDto tradeDto, Set<OrderEntity> orders);

    OrderDto toDto(OrderEntity orderEntity);

    @Mapping(target = "tradeId", ignore = true)
    OrderEntity toEntity(OrderDto orders);

    Set<OrderEntity> toEntities(Collection<OrderDto> orders);
}
