package org.dharbar.telegabot.facade.mapper;

import org.dharbar.telegabot.controller.request.CreateOrderRequest;
import org.dharbar.telegabot.controller.request.CreatePriceTriggerRequest;
import org.dharbar.telegabot.controller.request.UpdateOrderRequest;
import org.dharbar.telegabot.controller.request.UpdatePositionRequest;
import org.dharbar.telegabot.controller.request.UpdatePriceTriggerRequest;
import org.dharbar.telegabot.controller.response.AlarmResponse;
import org.dharbar.telegabot.controller.response.OrderResponse;
import org.dharbar.telegabot.controller.response.PositionResponse;
import org.dharbar.telegabot.controller.response.PriceTriggerResponse;
import org.dharbar.telegabot.repository.entity.OrderType;
import org.dharbar.telegabot.service.position.dto.AlarmDto;
import org.dharbar.telegabot.service.position.dto.OrderDto;
import org.dharbar.telegabot.service.position.dto.PositionDto;
import org.dharbar.telegabot.service.position.dto.PriceTriggerDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.UUID;

@Mapper(componentModel = "spring", imports = {OrderType.class})
public interface PositionFacadeMapper {

    @Mapping(target = "currentRatePrice", ignore = true)
    @Mapping(target = "currentNetProfitAmount", ignore = true)
    @Mapping(target = "currentProfitPercentage", ignore = true)
    PositionResponse toResponse(PositionDto positionDto);

    OrderResponse toResponse(OrderDto dto);
    PriceTriggerResponse toResponse(PriceTriggerDto dto);
    AlarmResponse toResponse(AlarmDto dto);

    @Mapping(target = "id", ignore = true)
    PriceTriggerDto toDto(CreatePriceTriggerRequest request);
    Set<PriceTriggerDto> toDtoPriceTriggers(Set<CreatePriceTriggerRequest> requests);

    @Mapping(target = "id", ignore = true)
    OrderDto toDto(CreateOrderRequest orders);
    Set<OrderDto> toDtoOrders(Set<CreateOrderRequest> orders);

    OrderDto toDto(UUID id, CreateOrderRequest orders);

    Set<OrderDto> toDtoUpdateOrders(Set<UpdateOrderRequest> orders);
    OrderDto toDto(UpdateOrderRequest orders);

    Set<PriceTriggerDto> toDtoUpdatePriceTriggers(Set<UpdatePriceTriggerRequest> requests);
    PriceTriggerDto toDto(UpdatePriceTriggerRequest request);

    @Mapping(target = "sellTotalAmount", ignore = true)
    @Mapping(target = "sellQuantity", ignore = true)
    @Mapping(target = "sellAveragePrice", ignore = true)
    @Mapping(target = "profitPercentage", ignore = true)
    @Mapping(target = "openAt", ignore = true)
    @Mapping(target = "netProfitAmount", ignore = true)
    @Mapping(target = "isClosed", ignore = true)
    @Mapping(target = "commissionTotalAmount", ignore = true)
    @Mapping(target = "closedAt", ignore = true)
    @Mapping(target = "buyTotalAmount", ignore = true)
    @Mapping(target = "buyQuantity", ignore = true)
    @Mapping(target = "buyAveragePrice", ignore = true)
    PositionDto toDto(UUID id, UpdatePositionRequest request, Set<OrderDto> orders, Set<PriceTriggerDto> priceTriggerDtos);
}
