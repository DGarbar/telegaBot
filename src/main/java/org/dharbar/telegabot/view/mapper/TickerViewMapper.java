package org.dharbar.telegabot.view.mapper;

import org.dharbar.telegabot.controller.request.CreateTickerRequest;
import org.dharbar.telegabot.repository.entity.TickerType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TickerViewMapper {

    CreateTickerRequest toCreateTickerRequest(String ticker, TickerType type);
}
