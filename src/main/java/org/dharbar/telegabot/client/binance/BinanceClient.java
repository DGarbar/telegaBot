package org.dharbar.telegabot.client.binance;

import org.dharbar.telegabot.client.binance.config.BinanceFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "binance-client", url = "${api.binance.url}", configuration = BinanceFeignConfig.class)
public interface BinanceClient {

    // BTCUSDT
    // @GetMapping(value = "/api/v3/ticker/price")
    // List<BinanceP2pBuy> getTransactions(@RequestBody String symbol);
}
