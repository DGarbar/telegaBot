package org.dharbar.telegabot.service.stockprice;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.client.ttingo.TiingoClient;
import org.dharbar.telegabot.client.ttingo.dto.TiingoQuoteResponse;
import org.dharbar.telegabot.repository.StockPriceRepository;
import org.dharbar.telegabot.repository.entity.StockPriceEntity;
import org.dharbar.telegabot.service.stockprice.dto.StockPriceDto;
import org.dharbar.telegabot.service.stockprice.mapper.StockPriceMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockPriceService {

    private final TiingoClient exchangeQuoteProvider;
    private final StockPriceRepository stockPriceRepository;

    private final StockPriceMapper stockPriceMapper;

    public List<StockPriceDto> findAll() {
        return stockPriceRepository.findAll().stream()
                .map(stockPriceMapper::toDto)
                .collect(Collectors.toList());
    }

    // TODO maybe ignore that has same EOD
    public void updateEndOfDayStockPriceFromProvider(String ticker) {
        stockPriceRepository.findById(ticker)
                .ifPresentOrElse(
                        this::updateStockPrice,
                        () -> createStockPrice(ticker));
    }

    public StockPriceDto createStockPrice(String ticker) {
        TiingoQuoteResponse response = getProviderResponse(ticker);
        StockPriceEntity stockPrice = stockPriceMapper.toNewEntity(ticker, response);
        StockPriceEntity savedStockPrice = stockPriceRepository.save(stockPrice);
        return stockPriceMapper.toDto(savedStockPrice);
    }

    private void updateStockPrice(StockPriceEntity stockPriceEntity) {
        TiingoQuoteResponse response = getProviderResponse(stockPriceEntity.getTicker());
        StockPriceEntity updatedEntity = stockPriceMapper.toEntity(response, stockPriceEntity);
        stockPriceRepository.save(updatedEntity);
    }

    private TiingoQuoteResponse getProviderResponse(String ticker) {
        return exchangeQuoteProvider.getLatestPrice(ticker).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No quote found for " + ticker));
    }

}
