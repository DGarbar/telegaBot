package org.dharbar.telegabot.service.rate.dto;

import lombok.Builder;
import lombok.Value;

import java.util.Currency;

@Value
@Builder
public class RateDto {
    Currency currencyFrom;
    Currency currencyTo;
    double rateBuy;
    double rateSell;
    RateProvider rateProvider;

    double amount;
}
