package org.dharbar.telegabot.service.stockprice;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.client.ttingo.TiingoClient;
import org.dharbar.telegabot.client.ttingo.dto.TiingoQuoteResponse;
import org.dharbar.telegabot.repository.StockPriceRepository;
import org.dharbar.telegabot.repository.entity.StockPriceEntity;
import org.dharbar.telegabot.service.stockprice.mapper.StockPriceMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockPriceService {

    private final TiingoClient exchangeQuoteProvider;
    private final StockPriceRepository stockPriceRepository;

    private final StockPriceMapper stockPriceMapper;

    // TODO maybe ignore that has date for
    public void updateEndOfDayStockPriceFromProvider(String ticker) {
        TiingoQuoteResponse tiingoQuoteResponse = exchangeQuoteProvider.getLatestPrice(ticker).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No quote found for " + ticker));

        stockPriceRepository.findById(ticker)
                .ifPresentOrElse(
                        stockPriceEntity -> updateStockPrice(stockPriceEntity, tiingoQuoteResponse),
                        () -> createStockPrice(ticker, tiingoQuoteResponse));
    }

    private void updateStockPrice(StockPriceEntity stockPriceEntity, TiingoQuoteResponse response) {
        StockPriceEntity updatedEntity = stockPriceMapper.toEntity(response, stockPriceEntity);
        stockPriceRepository.save(updatedEntity);
    }

    private void createStockPrice(String ticker, TiingoQuoteResponse response) {
        StockPriceEntity stockPrice = stockPriceMapper.toNewEntity(ticker, response);
        stockPriceRepository.save(stockPrice);
    }

}
