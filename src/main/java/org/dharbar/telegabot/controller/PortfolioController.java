package org.dharbar.telegabot.controller;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.controller.request.CreatePortfolioRequest;
import org.dharbar.telegabot.controller.response.PortfolioResponse;
import org.dharbar.telegabot.facade.PortfolioFacade;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/portfolios")
public class PortfolioController {

    private final PortfolioFacade portfolioFacade;

    @GetMapping
    public List<PortfolioResponse> getPortfolios() {
        return portfolioFacade.getPortfolios();
    }

    @PostMapping
    public PortfolioResponse createPortfolio(@RequestBody CreatePortfolioRequest request) {
        return portfolioFacade.createPortfolio(request);
    }

    @PutMapping("/{id}")
    public PortfolioResponse updatePortfolio(@PathVariable UUID id, @RequestBody CreatePortfolioRequest request) {
        return portfolioFacade.updatePortfolio(id, request);
    }

    // @PostMapping("/{id}/orders")
    // public PositionResponse addOrderToPosition(@PathVariable UUID id, CreateOrderRequest request) {
    //     return positionsFacade.addPositionNewOrder(id, request);
    // }
}
