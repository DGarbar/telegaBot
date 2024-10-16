package org.dharbar.telegabot.controller;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.controller.filter.PositionFilter;
import org.dharbar.telegabot.controller.request.CreateOrderRequest;
import org.dharbar.telegabot.controller.request.CreatePositionRequest;
import org.dharbar.telegabot.controller.request.UpdatePositionRequest;
import org.dharbar.telegabot.controller.response.PositionResponse;
import org.dharbar.telegabot.facade.PositionFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping()
public class PositionController {

    private final PositionFacade positionsFacade;

    @GetMapping("/api/positions/{id}")
    public PositionResponse getPosition(@PathVariable UUID id) {
        return positionsFacade.getPosition(id);
    }

    @GetMapping("/api/positions")
    public Page<PositionResponse> getPositions(@RequestParam(required = false) String ticker,
                                               @RequestParam(required = false) UUID portfolioId,
                                               @RequestParam(required = false) Boolean isClosed,
                                               @PageableDefault(size = 40, sort = "openAt", direction = Sort.Direction.DESC) Pageable pageRequest) {
        PositionFilter positionFilter = PositionFilter.builder()
                .ticker(ticker)
                .portfolioId(portfolioId)
                .isClosed(isClosed)
                .build();
        return positionsFacade.getPositions(positionFilter, pageRequest);
    }

    @PostMapping("/api/positions")
    public PositionResponse createPosition(@RequestBody CreatePositionRequest request) {
        return positionsFacade.createPosition(request);
    }

    @PutMapping("/api/positions/{id}")
    public PositionResponse updatePosition(@PathVariable UUID id, @RequestBody UpdatePositionRequest request) {
        return positionsFacade.updatePosition(id, request);
    }

    @PutMapping("/api/positions/{id}:recalculate")
    public PositionResponse recalculatePosition(@PathVariable UUID id) {
        return positionsFacade.recalculatePosition(id);
    }

    @PostMapping("/api/positions/{id}/orders")
    public PositionResponse addOrderToPosition(@PathVariable UUID id, CreateOrderRequest request) {
        return positionsFacade.addPositionNewOrder(id, request);
    }

    @PutMapping("/api/positions/{id}/orders/{orderId}")
    public PositionResponse updatePositionOrder(@PathVariable UUID id,
                                                @PathVariable UUID orderId,
                                                CreateOrderRequest request) {
        return positionsFacade.updatePositionOrder(id, orderId, request);
    }

    @DeleteMapping("/api/positions/{id}/alarms/{alarmId}")
    public void deleteAlarm(@PathVariable UUID id, @PathVariable UUID alarmId) {
        positionsFacade.deletePositionAlarm(id, alarmId);
    }
}
