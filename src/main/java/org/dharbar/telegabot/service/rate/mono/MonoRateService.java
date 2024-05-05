package org.dharbar.telegabot.service.rate.mono;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.client.mono.MonoClient;
import org.dharbar.telegabot.client.mono.response.RateMonoResponse;
import org.dharbar.telegabot.service.rate.dto.RateDto;
import org.dharbar.telegabot.service.rate.dto.RateProvider;
import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MonoRateService {

    private final MonoClient monoApi;

    public List<RateDto> getRates(List<Currency> currencyFrom, Currency currencyTo) {
        List<RateMonoResponse> rates = monoApi.getRates();
        return rates.stream()
                .filter(rate -> currencyFrom.contains(rate.getCurrencyFrom()))
                .filter(rate -> currencyTo.equals(rate.getCurrencyTo()))
                .map(this::toRate)
                .toList();
    }

    private RateDto toRate(RateMonoResponse rate) {
        return RateDto.builder()
                .currencyFrom(rate.getCurrencyFrom())
                .currencyTo(rate.getCurrencyTo())
                .rateBuy(rate.getRateBuy())
                .rateSell(rate.getRateSell())
                .rateProvider(rateProvider())
                .build();
    }

    private RateProvider rateProvider(){
        return RateProvider.MONO;
    }
}
