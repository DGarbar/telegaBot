package org.dharbar.telegabot.service.ticker.tickerprice.binance;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.client.binance.BinanceClient;
import org.dharbar.telegabot.client.binance.response.BinanceTickerPriceResponse;
import org.dharbar.telegabot.repository.entity.TickerType;
import org.dharbar.telegabot.service.ticker.dto.TickerPrice;
import org.dharbar.telegabot.service.ticker.mapper.TickerPriceMapper;
import org.dharbar.telegabot.service.ticker.tickerprice.TickerPriceProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BinanceService implements TickerPriceProvider {
    private final BinanceClient binanceClient;

    private final TickerPriceMapper tickerPriceMapper;

    @Override
    public TickerPrice getLatestPrice(String ticker) {
        BinanceTickerPriceResponse tickerPrice = binanceClient.getTickerPrice(ticker + "USDT");
        return tickerPriceMapper.toDto(tickerPrice);
    }

    @Override
    public Map<String, TickerPrice> getLatestPrices(List<String> tickers) {
        List<String> binanceTickers = tickers.stream().map(ticker -> ticker + "USDT").toList();
        return binanceClient.getTickerPrices(binanceTickers).stream()
                .map(tickerPriceMapper::toDto)
                .collect(Collectors.toMap(TickerPrice::getTicker, Function.identity()));
    }

    @Override
    public TickerType getTickerType() {
        return TickerType.CRYPTO;
    }
}
