package org.dharbar.telegabot.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dharbar.telegabot.service.ticker.TickerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TickerPriceTaskService {

    private final TickerService tickerService;

    @Scheduled(cron = "${task.eod-ticker-price-update}")
    public void updateEodTickerPrice() {
        log.info("Updating end of day ticker prices");
        tickerService.updateEndOfDayTickerPricesFromProvider();
        log.info("End of day ticker prices updated");
    }

    @Scheduled(cron = "${task.current-ticker-price-update}")
    public void updateCurrentTickerPrice() {
        log.info("Updating current ticker prices");
        tickerService.updateCurrentTickerPricesFromProvider();
        log.info("Current ticker prices updated");
    }
}
