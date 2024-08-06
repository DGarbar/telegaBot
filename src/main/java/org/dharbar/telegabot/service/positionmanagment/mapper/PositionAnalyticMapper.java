package org.dharbar.telegabot.service.positionmanagment.mapper;

import org.dharbar.telegabot.repository.entity.OrderType;
import org.dharbar.telegabot.service.positionmanagment.dto.OrderDto;
import org.dharbar.telegabot.service.positionmanagment.dto.PositionAnalyticDto;
import org.dharbar.telegabot.service.positionmanagment.dto.PositionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;

@Mapper(componentModel = "spring", imports = {OrderType.class})
public interface PositionAnalyticMapper {

    @Mapping(target = "byuOrder", expression = "java(findOrder(positionDto.getOrders(), OrderType.BUY))")
    @Mapping(target = "sellOrder", expression = "java(findOrder(positionDto.getOrders(), OrderType.SELL))")
    PositionAnalyticDto toDto(PositionDto positionDto);

    default OrderDto findOrder(Collection<OrderDto> orders, OrderType type) {
        return orders.stream()
                .filter(order -> type == order.getType())
                .findFirst()
                .orElse(null);
    }
}
