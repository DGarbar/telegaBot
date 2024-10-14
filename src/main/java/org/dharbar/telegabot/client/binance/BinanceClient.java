package org.dharbar.telegabot.client.binance;

import org.dharbar.telegabot.client.binance.config.BinanceFeignConfig;
import org.dharbar.telegabot.client.binance.response.BinanceTickerPriceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "binance-client", url = "${api.binance.url}", configuration = BinanceFeignConfig.class)
public interface BinanceClient {

    // BTCUSDT
    @GetMapping(value = "/api/v3/ticker/price")
    BinanceTickerPriceResponse getTickerPrice(@RequestParam String symbol);

    // [BTCUSDT, ETHUSDT]
    @GetMapping(value = "/api/v3/ticker/price")
    List<BinanceTickerPriceResponse> getTickerPrices(@RequestParam List<String> symbols);
}
