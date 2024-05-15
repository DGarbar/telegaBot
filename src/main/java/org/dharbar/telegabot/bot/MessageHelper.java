package org.dharbar.telegabot.bot;

import org.dharbar.telegabot.service.rate.dto.RateDto;
import org.dharbar.telegabot.service.rate.dto.RateProvider;

import java.util.Currency;
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
        return String.format("<b>%s</b>:\n%s\n", rateProvider, rateMessage);
    }

    private static String toRateMessage(RateDto rate) {
        StringBuilder message = new StringBuilder();

        Currency currencyFrom = rate.getCurrencyFrom();
        Currency currencyTo = rate.getCurrencyTo();
        double rateBuy = rate.getRateBuy();
        double rateSell = rate.getRateSell();

        if (Currency.getInstance("UAH").equals(currencyTo)) {
            message.append(currencyFrom).append(" = ");
        } else {
            message.append(currencyFrom).append(" -> ").append(currencyTo).append(" = ");
        }

        if (rateSell == 0 || rateBuy == 0) {
            double price = rateSell == 0 ? rateBuy : rateSell;
            message.append(String.format("%.2f", price));
        } else {
            message.append(String.format("%.2f ::: %.2f", rateBuy, rateSell));
        }

        return message.toString();
    }

}
