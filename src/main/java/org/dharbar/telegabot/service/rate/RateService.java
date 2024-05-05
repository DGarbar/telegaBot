package org.dharbar.telegabot.service.rate;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.client.rate.RateApi;
import org.dharbar.telegabot.client.rate.dto.RateResponse;
import org.dharbar.telegabot.service.dto.Currency;
import org.dharbar.telegabot.service.dto.RateDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RateService {

    private final RateApi rateServiceApi;

    public RateDto getCurrencyRate(Currency currency) {
        List<RateResponse> rateByCurrency = rateServiceApi.getRateByCurrency(currency, "20240316", "json");
        RateResponse rateResponse = rateByCurrency.get(0);
        return new RateDto(rateResponse.getCurrency(), rateResponse.getRate());
    }
}
