package org.dharbar.telegabot.task;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.service.stockprice.StockPriceService;
import org.dharbar.telegabot.service.stockprice.dto.StockPriceDto;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TradeTaskService {

    private final StockPriceService stockPriceService;

    @Scheduled(cron = "${task.stock-price-update}")
    public void updateStockPrice() {
        stockPriceService.findAll().stream()
                .map(StockPriceDto::getTicker)
                .forEach(stockPriceService::updateEndOfDayStockPriceFromProvider);
    }
}
