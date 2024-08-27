package org.dharbar.telegabot.service.rate.binance;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.client.binance.BinanceP2pClient;
import org.dharbar.telegabot.client.binance.request.BinanceP2pOrderRequest;
import org.dharbar.telegabot.client.binance.response.BinanceP2pResult;
import org.dharbar.telegabot.service.rate.dto.RateDto;
import org.dharbar.telegabot.service.rate.dto.RateProvider;
import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BinanceRateService {

    private final BinanceP2pClient binanceP2pClient;

    public List<RateDto> getRates(List<Currency> currencyFrom, Currency currencyTo) {
        BinanceP2pOrderRequest request = BinanceP2pOrderRequest.builder()
                .fiat(currencyTo.getCurrencyCode())
                .page(1)
                .rows(15)
                .tradeType("BUY")
                .asset("USDT")
                .filterType("ALL")
                .build();

        BinanceP2pResult result = binanceP2pClient.getOrder(request);
        if (!result.isSuccess()) {
            throw new RuntimeException("Error while getting rates from Binance");
        }

        return result.getData().stream()
                .limit(5)
                .map(BinanceP2pResult.P2pOrder::getAdv)
                .map(advertise -> RateDto.builder()
                        .currencyFrom(Currency.getInstance("USD"))
                        .currencyTo(Currency.getInstance(advertise.getFiatUnit()))
                        .rateSell(advertise.getPrice().doubleValue())
                        .rateProvider(rateProvider())
                        .amount(advertise.getMaxSingleTransAmount().doubleValue())
                        .build())
                .toList();
    }

    private RateProvider rateProvider() {
        return RateProvider.BINANCE;
    }
}
