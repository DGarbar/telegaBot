package org.dharbar.telegabot.service.rate;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.service.rate.binance.BinanceRateService;
import org.dharbar.telegabot.service.rate.dto.RateDto;
import org.dharbar.telegabot.service.rate.dto.RateProvider;
import org.dharbar.telegabot.service.rate.mono.MonoRateService;
import org.dharbar.telegabot.service.rate.nbu.NbuService;
import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.List;
import java.util.Map;

import static org.dharbar.telegabot.service.rate.dto.RateProvider.BINANCE;
import static org.dharbar.telegabot.service.rate.dto.RateProvider.MONO;
import static org.dharbar.telegabot.service.rate.dto.RateProvider.NBU;

@Service
@RequiredArgsConstructor
public class RateService {

    public static final Currency UAH = Currency.getInstance("UAH");
    public static final List<Currency> CURRENCY_FROM = List.of(Currency.getInstance("USD"), Currency.getInstance("EUR"));

    private final NbuService nbuRateService;
    private final MonoRateService monoRateService;
    private final BinanceRateService binanceRateService;

    public Map<RateProvider, List<RateDto>> getFiatCurrencyRates() {
        List<RateDto> nbuRates = nbuRateService.getRates(CURRENCY_FROM, UAH);
        List<RateDto> monoRates = monoRateService.getRates(CURRENCY_FROM, UAH);

        return Map.of(
                NBU, nbuRates,
                MONO, monoRates);
    }

    public Map<RateProvider, List<RateDto>> getCryptoRates() {
        return Map.of(BINANCE, binanceRateService.getRates(CURRENCY_FROM, UAH));
    }

}
