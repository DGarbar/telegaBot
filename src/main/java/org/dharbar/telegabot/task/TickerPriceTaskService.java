package org.dharbar.telegabot.task;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.service.ticker.TickerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TickerPriceTaskService {

    private final TickerService tickerService;

    @Scheduled(cron = "${task.ticker-price-update}")
    public void updateTickerPrice() {
        tickerService.updateEndOfDayTickerPricesFromProvider();
    }
}
