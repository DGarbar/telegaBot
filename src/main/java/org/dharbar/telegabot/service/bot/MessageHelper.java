package org.dharbar.telegabot.service.bot;

import org.dharbar.telegabot.service.rate.dto.RateDto;
import org.dharbar.telegabot.service.rate.dto.RateProvider;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MessageHelper {

    public static String rateMessage(Map<RateProvider, List<RateDto>> currencyRates) {
        return currencyRates.entrySet().stream()
                .map(entry -> toProviderRates(entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("\n"));
    }

    private static String toProviderRates(RateProvider rateProvider, List<RateDto> rates) {
        String rateMessage = rates.stream()
                .map(MessageHelper::toRateMessage)
                .collect(Collectors.joining("\n"));
        return String.format("%s:\n%s", rateProvider, rateMessage);
    }

    private static String toRateMessage(RateDto rate) {
        if (rate.getRateSell() == 0) {
            return String.format("%s -> %s  =  %.2f", rate.getCurrencyFrom(), rate.getCurrencyTo(), rate.getRateBuy());
        }
        return String.format("%s -> %s  =  %.2f ::: %.2f", rate.getCurrencyFrom(), rate.getCurrencyTo(), rate.getRateBuy(), rate.getRateSell());
    }

}
