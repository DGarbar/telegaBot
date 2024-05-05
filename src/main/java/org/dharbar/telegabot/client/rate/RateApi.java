package org.dharbar.telegabot.client.rate;

import org.dharbar.telegabot.client.rate.dto.RateResponse;
import org.dharbar.telegabot.service.dto.Currency;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "rate-client", url = "${api.rate.url}")
public interface RateApi {

    @GetMapping(value = "/exchange?valcode=EUR&date=20240316&json")
    List<RateResponse> getRateByCurrency(@RequestParam("valcode") Currency currency,
                                         @RequestParam("date") String date,
                                         @RequestParam("json") String json);
}
