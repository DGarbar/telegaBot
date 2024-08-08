package org.dharbar.telegabot.controller;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.facade.StockPriceFacade;
import org.dharbar.telegabot.service.stockprice.dto.StockPriceDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class StockPriceController {

    private final StockPriceFacade stockPriceFacade;

    @GetMapping("/stock-prices")
    public List<StockPriceDto> getStockPrices() {
        return stockPriceFacade.getAll();
    }

    @PostMapping("/stock-prices")
    public StockPriceDto createStockPrice(@RequestBody String ticker) {
        return stockPriceFacade.createStockPrice(ticker);
    }

}
