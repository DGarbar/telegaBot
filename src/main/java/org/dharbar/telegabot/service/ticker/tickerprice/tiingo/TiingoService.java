package org.dharbar.telegabot.service.ticker.tickerprice.tiingo;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.client.ttingo.TiingoClient;
import org.dharbar.telegabot.client.ttingo.dto.TiingoQuoteResponse;
import org.dharbar.telegabot.repository.entity.TickerType;
import org.dharbar.telegabot.service.ticker.dto.TickerPrice;
import org.dharbar.telegabot.service.ticker.dto.TickerRangePrice;
import org.dharbar.telegabot.service.ticker.mapper.TickerPriceMapper;
import org.dharbar.telegabot.service.ticker.tickerprice.TickerPriceProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TiingoService implements TickerPriceProvider {

    private final TiingoClient stockExchangeQuoteProvider;

    private final TickerPriceMapper tickerPriceMapper;

    @Override
    public TickerPrice getLatestPrice(String ticker) {
        TiingoQuoteResponse rateDto = getEodPrices(ticker);
        return tickerPriceMapper.toDto(ticker, rateDto);
    }

    @Override
    public Map<String, TickerPrice> getLatestPrices(List<String> tickers) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Map<String, TickerRangePrice> getDayPrices(List<String> tickers) {
       return tickers.stream()
                .map(ticker -> tickerPriceMapper.toDayDto(ticker, getEodPrices(ticker)))
                .collect(Collectors.toMap(TickerRangePrice::getTicker, Function.identity()));
    }

    private TiingoQuoteResponse getEodPrices(String ticker) {
        // EOD price only
        return stockExchangeQuoteProvider.getLatestPrice(ticker).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No quote found for " + ticker));
    }

    @Override
    public TickerType getTickerType() {
        return TickerType.STOCK;
    }
}
