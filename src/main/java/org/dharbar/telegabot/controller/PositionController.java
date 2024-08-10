package org.dharbar.telegabot.controller;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.controller.request.CreateOrderRequest;
import org.dharbar.telegabot.controller.request.CreatePositionRequest;
import org.dharbar.telegabot.controller.response.PositionResponse;
import org.dharbar.telegabot.facade.PositionFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/positions")
public class PositionController {

    private final PositionFacade positionsFacade;

    @GetMapping
    public Page<PositionResponse> getPositions(@RequestParam(defaultValue = "true") boolean isOpen,
                                               @PageableDefault(size = 40, sort = "openAt", direction = Sort.Direction.DESC) Pageable pageRequest) {
        return isOpen
                ? positionsFacade.getOpenPositions(pageRequest)
                : positionsFacade.getPositions(pageRequest);
    }

    @PostMapping
    public PositionResponse createPosition(@RequestBody CreatePositionRequest request) {
        return positionsFacade.createPosition(request);
    }

    @PostMapping("/{id}/orders")
    public PositionResponse addOrderToPosition(@PathVariable UUID id, CreateOrderRequest request) {
        return positionsFacade.addPositionNewOrder(id, request);
    }
}
