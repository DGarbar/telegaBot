package org.dharbar.telegabot.service.positionmanagment.mapper;

import org.dharbar.telegabot.controller.request.CreateOrderRequest;
import org.dharbar.telegabot.controller.response.OrderResponse;
import org.dharbar.telegabot.controller.response.PositionResponse;
import org.dharbar.telegabot.repository.entity.OrderType;
import org.dharbar.telegabot.service.positionmanagment.dto.OrderDto;
import org.dharbar.telegabot.service.positionmanagment.dto.PositionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", imports = {OrderType.class})
public interface PositionFacadeMapper {

    @Mapping(target = "currentRatePrice", ignore = true)
    @Mapping(target = "currentNetProfitAmount", ignore = true)
    @Mapping(target = "currentProfitPercentage", ignore = true)
    PositionResponse toResponse(PositionDto positionDto);

    OrderResponse toResponse(OrderDto orderDto);

    List<OrderDto> toDtos(List<CreateOrderRequest> orders);

    @Mapping(target = "id", ignore = true)
    OrderDto toDto(CreateOrderRequest orders);
}
