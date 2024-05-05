package org.dharbar.telegabot.client.nbu;

import org.dharbar.telegabot.client.nbu.config.NbuFeignConfig;
import org.dharbar.telegabot.client.nbu.dto.RateNbuResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "rate-client", url = "${api.rate.url}",configuration = NbuFeignConfig.class)
public interface NbuApi {

    @GetMapping(value = "/exchange")
    List<RateNbuResponse> getRateByCurrency(@RequestParam("valcode") String currencyCode,
                                            @RequestParam("date") String date);

    @GetMapping(value = "/exchange")
    List<RateNbuResponse> getCurrentRates();
}
