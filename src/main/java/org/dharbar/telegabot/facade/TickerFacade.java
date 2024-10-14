package org.dharbar.telegabot.facade;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.repository.entity.TickerType;
import org.dharbar.telegabot.service.ticker.TickerService;
import org.dharbar.telegabot.service.ticker.dto.TickerDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class TickerFacade {

    private final TickerService tickerService;

    @Transactional(readOnly = true)
    public Set<TickerDto> getAll() {
        return tickerService.getAll();
    }

    @Transactional
    public TickerDto createTicker(String ticker, TickerType type) {
        return tickerService.createTicker(ticker, type);
    }
}
