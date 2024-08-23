package org.dharbar.telegabot.service.positionmanagment.mapper;

import org.dharbar.telegabot.repository.entity.OrderEntity;
import org.dharbar.telegabot.repository.entity.OrderType;
import org.dharbar.telegabot.repository.entity.PositionEntity;
import org.dharbar.telegabot.service.positionmanagment.dto.OrderDto;
import org.dharbar.telegabot.service.positionmanagment.dto.PositionDto;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Collection;
import java.util.Set;

import static org.dharbar.telegabot.service.positionmanagment.PositionCalculationService.PositionCalculation;

@Mapper(componentModel = "spring", collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED, imports = {OrderType.class})
public interface PositionServiceMapper {

    PositionDto toDto(PositionEntity positions);

    @Mapping(target = "openAt", expression = "java(java.time.LocalDate.now())")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "closedAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    PositionEntity toNewEntity(String ticker, String comment, PositionCalculation calculation, Set<OrderEntity> orders);

    @Mapping(target = "isClosed", source = "calculation.isClosed")
    @Mapping(target = "closedAt", expression = "java(calculation.isClosed() ? java.time.LocalDate.now() : null)")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "openAt", ignore = true)
    @Mapping(target = "comment", ignore = true)
    @Mapping(target = "ticker", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(@MappingTarget PositionEntity position, Set<OrderEntity> orders, PositionCalculation calculation);

    OrderDto toDto(OrderEntity orders);

    Set<OrderDto> toDtos(Set<OrderEntity> orders);

    @Mapping(target = "position", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    OrderEntity toEntity(OrderDto orderDtos);

    Set<OrderEntity> toEntities(Collection<OrderDto> orderDtos);
}
