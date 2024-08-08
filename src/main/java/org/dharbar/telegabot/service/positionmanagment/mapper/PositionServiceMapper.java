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
public interface PositionServiceMapper {

    PositionDto toDto(PositionEntity positionEntity);

    @Mapping(target = "dateAt", expression = "java(java.time.LocalDate.now())")
    PositionEntity toNewEntity(PositionDto positionDto, Set<OrderEntity> orders);
    PositionEntity toEntity(PositionDto positionDto, Set<OrderEntity> orders);

    OrderDto toDto(OrderEntity orderEntity);

    @Mapping(target = "positionId", ignore = true)
    OrderEntity toEntity(OrderDto orders);

    Set<OrderEntity> toEntities(Collection<OrderDto> orders);
}
