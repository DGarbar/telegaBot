package org.dharbar.telegabot.service.ticker.tickerprice;

import org.dharbar.telegabot.repository.entity.TickerType;
import org.dharbar.telegabot.service.ticker.dto.TickerPrice;
import org.dharbar.telegabot.service.ticker.dto.TickerRangePrice;

import java.util.List;
import java.util.Map;

public interface TickerPriceProvider {

    TickerPrice getLatestPrice(String ticker);

    Map<String, TickerPrice> getLatestPrices(List<String> tickers);

    Map<String, TickerRangePrice> getDayPrices(List<String> tickers);

    TickerType getTickerType();
}
