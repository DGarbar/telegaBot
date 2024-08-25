package org.dharbar.telegabot.view.mapper;

import org.dharbar.telegabot.controller.request.CreateOrderRequest;
import org.dharbar.telegabot.controller.request.CreatePositionRequest;
import org.dharbar.telegabot.controller.response.OrderResponse;
import org.dharbar.telegabot.controller.response.PositionResponse;
import org.dharbar.telegabot.repository.entity.OrderType;
import org.dharbar.telegabot.view.model.OrderViewModel;
import org.dharbar.telegabot.view.model.PositionViewModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {OrderType.class})
public interface PositionViewMapper {

    @Mapping(target = "positionId", source = "positionId")
    OrderViewModel toModel(OrderResponse response, UUID positionId);

    default List<OrderViewModel> toModels(List<OrderResponse> responses, UUID positionId) {
        return responses.stream()
                .map(response -> toModel(response, positionId))
                .collect(Collectors.toList());
    }

    @Mapping(target = "orders", source = "orders")
    PositionViewModel toModel(PositionResponse response, List<OrderViewModel> orders);

    @Mapping(target = "comment", ignore = true)
    CreatePositionRequest toCreatePositionRequest(String ticker, UUID portfolioId, List<OrderViewModel> orders);

    CreateOrderRequest toCreateOrderRequest(OrderViewModel order);
}
