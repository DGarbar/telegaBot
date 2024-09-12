package org.dharbar.telegabot.service.rate.provider.nbu;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.client.nbu.NbuApi;
import org.dharbar.telegabot.service.rate.dto.RateDto;
import org.dharbar.telegabot.service.rate.mapper.RateServiceMapper;
import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NbuService {

    private final NbuApi nbuApi;
    private final RateServiceMapper rateServiceMapper;

    public List<RateDto> getRates(List<Currency> currencyFrom, Currency currencyTo) {
        return getRates().stream()
                .filter(rate -> currencyFrom.contains(rate.getCurrencyFrom()))
                .filter(rate -> currencyTo.equals(rate.getCurrencyTo()))
                .toList();
    }

    public List<RateDto> getRates() {
        return nbuApi.getCurrentRates().stream()
                .map(rateServiceMapper::toDto)
                .toList();
    }
}
