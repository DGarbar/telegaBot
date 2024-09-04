package org.dharbar.telegabot.view.mapper;

import org.dharbar.telegabot.controller.request.CreateOrderRequest;
import org.dharbar.telegabot.controller.request.CreatePositionRequest;
import org.dharbar.telegabot.controller.request.UpdateOrderRequest;
import org.dharbar.telegabot.controller.request.UpdatePositionRequest;
import org.dharbar.telegabot.controller.response.OrderResponse;
import org.dharbar.telegabot.controller.response.PositionResponse;
import org.dharbar.telegabot.repository.entity.OrderType;
import org.dharbar.telegabot.view.model.OrderViewModel;
import org.dharbar.telegabot.view.model.PositionViewModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {OrderType.class})
public interface PositionViewMapper {

    @Mapping(target = "positionId", source = "positionId")
    OrderViewModel toModel(OrderResponse response, UUID positionId);

    default Set<OrderViewModel> toModels(Set<OrderResponse> responses, UUID positionId) {
        return responses.stream()
                .map(response -> toModel(response, positionId))
                .collect(Collectors.toSet());
    }

    @Mapping(target = "orders", source = "orders")
    PositionViewModel toModel(PositionResponse response, Set<OrderViewModel> orders);

    @Mapping(target = "comment", ignore = true)
    CreatePositionRequest toCreatePositionRequest(String ticker, UUID portfolioId, Set<OrderViewModel> orders);

    @Mapping(target = "orders", source = "orderRequests")
    CreatePositionRequest toCreatePositionRequest(PositionViewModel position, Set<CreateOrderRequest> orderRequests);

    @Mapping(target = "orders", source = "orderRequests")
    UpdatePositionRequest toUpdatePositionRequest(PositionViewModel position, Set<UpdateOrderRequest> orderRequests);

    CreateOrderRequest toCreateOrderRequest(OrderViewModel order);
    Set<CreateOrderRequest> toCreateOrderRequests(Set<OrderViewModel> orders);

    UpdateOrderRequest toUpdateOrderRequest(OrderViewModel order);
    Set<UpdateOrderRequest> toUpdateOrderRequests(Set<OrderViewModel> orders);

}
