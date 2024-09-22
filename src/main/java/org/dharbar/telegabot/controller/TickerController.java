package org.dharbar.telegabot.controller;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.controller.request.CreateTickerRequest;
import org.dharbar.telegabot.facade.TickerFacade;
import org.dharbar.telegabot.service.ticker.dto.TickerDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tickers")
public class TickerController {

    private final TickerFacade tickerFacade;

    @GetMapping
    public Set<TickerDto> getTickers() {
        return tickerFacade.getAll();
    }

    @PostMapping
    public TickerDto createTicker(@RequestBody CreateTickerRequest request) {
        return tickerFacade.createTicker(request.getTicker(), request.getType());
    }

}
