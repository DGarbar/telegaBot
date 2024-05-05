package org.dharbar.telegabot.service.rate.nbu;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.client.nbu.NbuApi;
import org.dharbar.telegabot.client.nbu.dto.RateNbuResponse;
import org.dharbar.telegabot.service.rate.dto.RateDto;
import org.dharbar.telegabot.service.rate.dto.RateProvider;
import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NbuService {

    private final NbuApi nbuApi;

    public List<RateDto> getRates(List<Currency> currencyFrom, Currency currencyTo) {
        return getRates().stream()
                .filter(rate -> currencyFrom.contains(rate.getCurrencyFrom()))
                .filter(rate -> currencyTo.equals(rate.getCurrencyTo()))
                .toList();
    }

    public List<RateDto> getRates() {
        return nbuApi.getCurrentRates().stream()
                .map(this::toRate)
                .toList();
    }

    private RateDto toRate(RateNbuResponse rate) {
        return RateDto.builder()
                .currencyFrom(rate.getCurrency())
                .currencyTo(Currency.getInstance("UAH"))
                .rateBuy(rate.getRate())
                .rateProvider(rateProvider())
                .build();
    }

    private RateProvider rateProvider() {
        return RateProvider.NBU;
    }
}
