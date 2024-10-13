package org.dharbar.telegabot.service.position.mapper;

import org.dharbar.telegabot.repository.entity.OrderEntity;
import org.dharbar.telegabot.repository.entity.OrderType;
import org.dharbar.telegabot.repository.entity.PositionEntity;
import org.dharbar.telegabot.repository.entity.PositionType;
import org.dharbar.telegabot.repository.entity.PriceTriggerEntity;
import org.dharbar.telegabot.service.position.dto.OrderDto;
import org.dharbar.telegabot.service.position.dto.PositionDto;
import org.dharbar.telegabot.service.position.dto.PriceTriggerDto;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import static org.dharbar.telegabot.service.position.PositionCalculationService.PositionCalculation;

@Mapper(componentModel = "spring", collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED, imports = {OrderType.class})
public interface PositionServiceMapper {

    @Mapping(target = "openAt", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "alarms", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "closedAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    PositionEntity toNewEntity(String name,
                               String ticker,
                               PositionType type,
                               UUID portfolioId,
                               String comment,
                               PositionCalculation calculation,
                               Set<OrderEntity> orders,
                               Set<PriceTriggerEntity> priceTriggers);

    PositionDto toDto(PositionEntity positions);

    @Mapping(target = "portfolioId", ignore = true)
    @Mapping(target = "alarms", ignore = true)
    @Mapping(target = "isClosed", source = "calculation.isClosed")
    @Mapping(target = "closedAt", expression = "java(calculation.isClosed() ? java.time.LocalDate.now() : null)")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "openAt", ignore = true)
    @Mapping(target = "comment", ignore = true)
    @Mapping(target = "ticker", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(@MappingTarget PositionEntity position,
                      String name,
                      PositionType type,
                      Set<OrderEntity> orders,
                      Set<PriceTriggerEntity> priceTriggers,
                      PositionCalculation calculation);

    @Mapping(target = "name", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "priceTriggers", ignore = true)
    @Mapping(target = "portfolioId", ignore = true)
    @Mapping(target = "alarms", ignore = true)
    @Mapping(target = "isClosed", source = "calculation.isClosed")
    @Mapping(target = "closedAt", expression = "java(calculation.isClosed() ? java.time.LocalDate.now() : null)")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "openAt", ignore = true)
    @Mapping(target = "comment", ignore = true)
    @Mapping(target = "ticker", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updatePositionOrdersEntity(@MappingTarget PositionEntity position, Set<OrderEntity> orders, PositionCalculation calculation);

    @Mapping(target = "name", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "priceTriggers", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "portfolioId", ignore = true)
    @Mapping(target = "alarms", ignore = true)
    @Mapping(target = "isClosed", source = "calculation.isClosed")
    @Mapping(target = "closedAt", expression = "java(calculation.isClosed() ? java.time.LocalDate.now() : null)")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "openAt", ignore = true)
    @Mapping(target = "comment", ignore = true)
    @Mapping(target = "ticker", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateCalculatedEntity(@MappingTarget PositionEntity position, PositionCalculation calculation);

    OrderDto toDto(OrderEntity orders);

    Set<OrderDto> toDtos(Set<OrderEntity> orders);

    @Mapping(target = "position", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    OrderEntity toEntity(OrderDto orderDtos);

    Set<OrderEntity> toEntityOrders(Collection<OrderDto> orderDtos);

    @Mapping(target = "position", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(@MappingTarget OrderEntity order, OrderDto orderDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "position", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(@MappingTarget OrderEntity to, OrderEntity from);

    @Mapping(target = "position", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    PriceTriggerEntity toEntity(PriceTriggerDto priceTriggerDto);
    Set<PriceTriggerEntity> toEntityPriceTriggers(Collection<PriceTriggerDto> priceTriggerDtos);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "position", ignore = true)
    @Mapping(target = "isTriggered", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(@MappingTarget PriceTriggerEntity to, PriceTriggerEntity from);
}
