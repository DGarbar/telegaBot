package org.dharbar.telegabot.facade;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.service.stockprice.StockPriceService;
import org.dharbar.telegabot.service.stockprice.dto.StockPriceDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockPriceFacade {

    private final StockPriceService stockPriceService;

    @Transactional(readOnly = true)
    public List<StockPriceDto> getAll() {
        return stockPriceService.getAll();
    }

    public StockPriceDto createStockPrice(String ticker) {
        return stockPriceService.createStockPrice(ticker);
    }
}
