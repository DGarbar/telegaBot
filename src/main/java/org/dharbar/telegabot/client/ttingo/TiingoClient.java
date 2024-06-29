package org.dharbar.telegabot.client.ttingo;

import org.dharbar.telegabot.client.ttingo.config.TiingoFeignConfig;
import org.dharbar.telegabot.client.ttingo.dto.TiingoQuoteResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "tiingo-client", url = "${api.tiingo.url}", configuration = TiingoFeignConfig.class)
public interface TiingoClient {

    @GetMapping("/daily/{ticker}/prices")
    List<TiingoQuoteResponse> getLatestPrice(@PathVariable("ticker") String ticker);

    @GetMapping("/daily/{ticker}/prices")
    List<TiingoQuoteResponse> getHistoricalPrices(@PathVariable("ticker") String ticker,
                                                  @RequestParam("startDate") String startDate,
                                                  @RequestParam("endDate") String endDate);


}
