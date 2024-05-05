package org.dharbar.telegabot.client.binance;

import org.dharbar.telegabot.client.binance.request.BinanceP2pOrderRequest;
import org.dharbar.telegabot.client.binance.response.BinanceP2pResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "binance-p2p-client", url = "${api.binancep2p.url}")
public interface BinanceP2pClient {

    @GetMapping(value = "/bapi/c2c/v2/friendly/c2c/adv/search")
    BinanceP2pResult getOrder(@RequestBody BinanceP2pOrderRequest request);
}
