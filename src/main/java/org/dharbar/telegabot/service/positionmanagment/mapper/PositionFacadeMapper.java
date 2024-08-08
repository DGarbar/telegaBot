package org.dharbar.telegabot.service.positionmanagment.mapper;

import org.dharbar.telegabot.controller.request.CreateOrderRequest;
import org.dharbar.telegabot.controller.request.CreatePositionRequest;
import org.dharbar.telegabot.controller.response.OrderResponse;
import org.dharbar.telegabot.controller.response.PositionResponse;
import org.dharbar.telegabot.repository.entity.OrderType;
import org.dharbar.telegabot.service.positionmanagment.dto.OrderDto;
import org.dharbar.telegabot.service.positionmanagment.dto.PositionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", imports = {OrderType.class})
public interface PositionFacadeMapper {

    @Mapping(target = "byuOrder", expression = "java(toResponse(findOrder(positionDto.getOrders(), OrderType.BUY)))")
    @Mapping(target = "sellOrder", expression = "java(toResponse(findOrder(positionDto.getOrders(), OrderType.SELL)))")
    PositionResponse toResponse(PositionDto positionDto);

    OrderResponse toResponse(OrderDto orderDto);

    default OrderDto findOrder(Collection<OrderDto> orders, OrderType type) {
        return orders.stream()
                .filter(order -> type == order.getType())
                .findFirst()
                .orElse(null);
    }

    List<OrderDto> toDtos(List<CreateOrderRequest> orders);
    OrderDto toDto(CreateOrderRequest orders);

    @Mapping(target = "orders", source = "orders")
    PositionDto toDto(CreatePositionRequest request, List<OrderDto> orders);
}
