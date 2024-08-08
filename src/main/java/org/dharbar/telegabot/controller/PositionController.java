package org.dharbar.telegabot.controller;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.controller.request.CreatePositionRequest;
import org.dharbar.telegabot.controller.response.PositionResponse;
import org.dharbar.telegabot.facade.PositionFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PositionController {

    private final PositionFacade positionsAnalyticFacade;

    @GetMapping("/positions")
    public Page<PositionResponse> getPositions(@RequestParam(defaultValue = "true") boolean isOpen,
                                               PageRequest pageRequest) {
        return isOpen
                ? positionsAnalyticFacade.getOpenPositions(pageRequest)
                : positionsAnalyticFacade.getPositions(pageRequest);
    }

    @PostMapping("/positions")
    public PositionResponse createPosition(@RequestBody CreatePositionRequest request) {
        return positionsAnalyticFacade.createPosition(request);
    }

    // @PostMapping("/positions/{id}/orders")
    // public PositionResponse addOrderToPosition(@RequestParam String id) {
    //     return null;
    // }


    // Create new position in portfolio and pass initial order
    //POST /portfolios/{}/positions


    // Create new order in position
    //POST /positions/{}/orders


    // Get positions with order in it
    //GET /positions-analytic/{}
}
