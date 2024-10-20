package org.dharbar.telegabot.client.binance.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BinanceDayResponse {

    String symbol;
    BigDecimal openPrice;
    BigDecimal highPrice;
    BigDecimal lowPrice;
    BigDecimal lastPrice;

    long openTime;
    long closeTime;
    long count;
}
