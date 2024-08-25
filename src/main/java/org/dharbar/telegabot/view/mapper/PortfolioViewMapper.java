package org.dharbar.telegabot.view.mapper;

import org.dharbar.telegabot.controller.request.CreatePortfolioRequest;
import org.dharbar.telegabot.controller.response.PortfolioResponse;
import org.dharbar.telegabot.view.model.PortfolioViewModel;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PortfolioViewMapper {

    PortfolioViewModel toModel(PortfolioResponse response);

    List<PortfolioViewModel> toModel(List<PortfolioResponse> response);

    CreatePortfolioRequest toCreatePortfolioRequest(String name, String description);
}
