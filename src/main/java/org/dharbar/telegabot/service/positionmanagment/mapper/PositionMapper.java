package org.dharbar.telegabot.service.positionmanagment.mapper;

import org.dharbar.telegabot.repository.entity.OrderEntity;
import org.dharbar.telegabot.repository.entity.OrderType;
import org.dharbar.telegabot.repository.entity.PositionEntity;
import org.dharbar.telegabot.service.positionmanagment.dto.OrderDto;
import org.dharbar.telegabot.service.positionmanagment.dto.PositionDto;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;
import java.util.Set;

@Mapper(componentModel = "spring", collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED, imports = {OrderType.class})
public interface PositionMapper {

    PositionDto toDto(PositionEntity positionEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profitPercentage", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "netProfitUsd", ignore = true)
    @Mapping(target = "isClosed", constant = "false")
    PositionEntity toNewEntity(OrderEntity orderDto);

    PositionEntity toEntity(PositionDto positionDto, Set<OrderEntity> orders);

    OrderDto toDto(OrderEntity orderEntity);

    @Mapping(target = "positionId", ignore = true)
    OrderEntity toEntity(OrderDto orders);

    Set<OrderEntity> toEntities(Collection<OrderDto> orders);
}
