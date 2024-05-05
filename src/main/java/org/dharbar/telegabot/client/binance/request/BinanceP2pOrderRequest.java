package org.dharbar.telegabot.client.binance.request;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BinanceP2pOrderRequest {

    String fiat;
    int page;
    int rows;
    String tradeType;
    String asset;
    String filterType;
}
