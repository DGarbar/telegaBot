package org.dharbar.telegabot.view.mapper;

import org.dharbar.telegabot.controller.request.CreateOrderRequest;
import org.dharbar.telegabot.controller.request.CreatePositionRequest;
import org.dharbar.telegabot.controller.request.CreatePriceTriggerRequest;
import org.dharbar.telegabot.controller.request.UpdateOrderRequest;
import org.dharbar.telegabot.controller.request.UpdatePositionRequest;
import org.dharbar.telegabot.controller.request.UpdatePriceTriggerRequest;
import org.dharbar.telegabot.controller.response.AlarmResponse;
import org.dharbar.telegabot.controller.response.OrderResponse;
import org.dharbar.telegabot.controller.response.PositionResponse;
import org.dharbar.telegabot.controller.response.PriceTriggerResponse;
import org.dharbar.telegabot.repository.entity.OrderType;
import org.dharbar.telegabot.view.model.AlarmViewModel;
import org.dharbar.telegabot.view.model.OrderViewModel;
import org.dharbar.telegabot.view.model.PositionViewModel;
import org.dharbar.telegabot.view.model.PriceTriggerViewModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {OrderType.class})
public interface PositionViewMapper {

    @Mapping(target = "positionId", source = "positionId")
    OrderViewModel toModel(OrderResponse response, UUID positionId);

    PriceTriggerViewModel toModel(PriceTriggerResponse response);

    @Mapping(target = "positionId", source = "positionId")
    AlarmViewModel toModel(AlarmResponse response, UUID positionId);

    default Set<OrderViewModel> toOrderModels(Set<OrderResponse> responses, UUID positionId) {
        return responses.stream()
                .map(response -> toModel(response, positionId))
                .collect(Collectors.toSet());
    }

    default Set<PriceTriggerViewModel> toPriceTriggerModels(Set<PriceTriggerResponse> responses, UUID positionId) {
        return responses.stream()
                .map(this::toModel)
                .collect(Collectors.toSet());
    }

    default Set<AlarmViewModel> toAlarmModels(Set<AlarmResponse> responses, UUID positionId) {
        return responses.stream()
                .map(alarmResponse -> toModel(alarmResponse, positionId))
                .collect(Collectors.toSet());
    }

    @Mapping(target = "orders", source = "orders")
    @Mapping(target = "priceTriggers", source = "priceTriggers")
    @Mapping(target = "alarms", source = "alarms")
    PositionViewModel toModel(PositionResponse response,
                              Set<OrderViewModel> orders,
                              Set<PriceTriggerViewModel> priceTriggers,
                              Set<AlarmViewModel> alarms);

    @Mapping(target = "comment", ignore = true)
    CreatePositionRequest toCreatePositionRequest(String ticker, UUID portfolioId, Set<OrderViewModel> orders, Set<PriceTriggerViewModel> priceTriggers);

    @Mapping(target = "orders", source = "orderRequests")
    @Mapping(target = "priceTriggers", source = "priceTriggers")
    CreatePositionRequest toCreatePositionRequest(PositionViewModel position, Set<CreateOrderRequest> orderRequests, Set<CreatePriceTriggerRequest> priceTriggers);

    @Mapping(target = "orders", source = "orderRequests")
    @Mapping(target = "priceTriggers", source = "priceTriggers")
    UpdatePositionRequest toUpdatePositionRequest(PositionViewModel position, Set<UpdateOrderRequest> orderRequests, Set<UpdatePriceTriggerRequest> priceTriggers);

    CreateOrderRequest toCreateOrderRequest(OrderViewModel model);
    Set<CreateOrderRequest> toCreateOrderRequests(Set<OrderViewModel> models);

    UpdateOrderRequest toUpdateOrderRequest(OrderViewModel model);
    Set<UpdateOrderRequest> toUpdateOrderRequests(Set<OrderViewModel> models);

    CreatePriceTriggerRequest toCreatePriceTriggerRequest(PriceTriggerViewModel model);
    Set<CreatePriceTriggerRequest> toCreatePriceTriggerRequests(Set<PriceTriggerViewModel> models);

    UpdatePriceTriggerRequest toUpdatePriceTriggerRequest(PriceTriggerViewModel model);
    Set<UpdatePriceTriggerRequest> toUpdatePriceTriggerRequests(Set<PriceTriggerViewModel> models);

}
