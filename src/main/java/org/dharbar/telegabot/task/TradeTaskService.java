package org.dharbar.telegabot.task;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.service.stockprice.StockPriceService;
import org.dharbar.telegabot.service.trademanagment.TradeService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TradeTaskService {

    // TODO Maybe pull out tickers from trade service
    private final TradeService tradeService;
    private final StockPriceService stockPriceService;

    @Scheduled(cron = "${task.stock-price-update}")
    public void updateStockPrice() {
        tradeService.getTickers().forEach(stockPriceService::updateEndOfDayStockPriceFromProvider);
    }
}
