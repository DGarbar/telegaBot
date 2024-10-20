package org.dharbar.telegabot.service.ticker.mapper;

import org.dharbar.telegabot.client.binance.response.BinanceDayResponse;
import org.dharbar.telegabot.client.binance.response.BinanceTickerPriceResponse;
import org.dharbar.telegabot.client.ttingo.dto.TiingoQuoteResponse;
import org.dharbar.telegabot.service.ticker.dto.TickerPrice;
import org.dharbar.telegabot.service.ticker.dto.TickerRangePrice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TickerPriceMapper {

    @Mapping(target = "price", source = "response.close")
    @Mapping(target = "priceUpdatedAt", source = "response.date")
    TickerPrice toDto(String ticker, TiingoQuoteResponse response);

    @Mapping(target = "firstPrice", source = "tickerPrice.open")
    @Mapping(target = "lastPrice", source = "tickerPrice.close")
    @Mapping(target = "highPrice", source = "tickerPrice.high")
    @Mapping(target = "lowPrice", source = "tickerPrice.low")
    @Mapping(target = "firstAt", expression = "java(xmlGregorianCalendarToLocalDateTime( zonedDateTimeToXmlGregorianCalendar( tickerPrice.getDate().minusDays(1))))")
    @Mapping(target = "lastAt", source = "tickerPrice.date")
    TickerRangePrice toDayDto(String ticker, TiingoQuoteResponse tickerPrice);

    @Mapping(target = "ticker", expression = "java(tickerPrice.getSymbol().replace(\"USDT\", \"\"))")
    @Mapping(target = "priceUpdatedAt", expression = "java(java.time.LocalDateTime.now())")
    TickerPrice toDto(BinanceTickerPriceResponse tickerPrice);

    @Mapping(target = "ticker", expression = "java(tickerPrice.getSymbol().replace(\"USDT\", \"\"))")
    @Mapping(target = "firstPrice", source = "openPrice")
    @Mapping(target = "lastPrice", source = "lastPrice")
    @Mapping(target = "firstAt", expression = "java(java.time.LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(tickerPrice.getOpenTime()), java.time.ZoneId.systemDefault()))")
    @Mapping(target = "lastAt", expression = "java(java.time.LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(tickerPrice.getCloseTime()), java.time.ZoneId.systemDefault()))")
    TickerRangePrice toDto(BinanceDayResponse tickerPrice);
}
