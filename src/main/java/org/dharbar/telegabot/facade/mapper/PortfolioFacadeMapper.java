package org.dharbar.telegabot.facade.mapper;

import org.dharbar.telegabot.controller.request.CreatePortfolioRequest;
import org.dharbar.telegabot.controller.response.PortfolioResponse;
import org.dharbar.telegabot.service.portfolio.dto.PortfolioDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PortfolioFacadeMapper {

    PortfolioResponse toResponse(PortfolioDto dto);

    @Mapping(target = "id", ignore = true)
    PortfolioDto toDto(CreatePortfolioRequest request);

}
