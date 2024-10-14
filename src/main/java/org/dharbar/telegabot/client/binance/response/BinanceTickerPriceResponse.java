package org.dharbar.telegabot.client.binance.response;

import lombok.Data;

@Data
public class BinanceTickerPriceResponse {

    private String symbol;
    private String price;
}
