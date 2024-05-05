package org.dharbar.telegabot.client.binance;

import org.dharbar.telegabot.client.binance.config.BinanceFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "binance-client", url = "${api.binance.url}", configuration = BinanceFeignConfig.class)
public interface BinanceClient {

    // @GetMapping(value = "/sapi/v1/c2c/orderMatch/listUserOrderHistory")
    // List<BinanceP2pBuy> getTransactions(@PathVariable TradeType tradeType);
}
