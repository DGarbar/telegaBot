package org.dharbar.telegabot.service.ticker.mapper;

import org.dharbar.telegabot.client.binance.response.BinanceTickerPriceResponse;
import org.dharbar.telegabot.client.ttingo.dto.TiingoQuoteResponse;
import org.dharbar.telegabot.service.ticker.dto.TickerPrice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TickerPriceMapper {

    @Mapping(target = "price", source = "response.close")
    @Mapping(target = "priceUpdatedAt", source = "response.date")
    TickerPrice toDto(String ticker, TiingoQuoteResponse response);

    @Mapping(target = "ticker", expression = "java(tickerPrice.getSymbol().replace(\"USDT\", \"\"))")
    @Mapping(target = "priceUpdatedAt", expression = "java(java.time.LocalDateTime.now())")
    TickerPrice toDto(BinanceTickerPriceResponse tickerPrice);
}
