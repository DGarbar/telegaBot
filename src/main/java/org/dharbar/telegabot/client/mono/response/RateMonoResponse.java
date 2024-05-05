package org.dharbar.telegabot.client.mono.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import java.util.Currency;

@Data
public class RateMonoResponse {

    @JsonProperty("currencyCodeA")
    private Currency currencyFrom;
    @JsonProperty("currencyCodeB")
    private Currency currencyTo;
    private long date;
    private double rateBuy;
    private double rateSell;

    @JsonSetter("currencyCodeA")
    public void setCurrencyFrom(Integer currencyCodeA) {
        this.currencyFrom = toCurrency(currencyCodeA);
    }

    @JsonSetter("currencyCodeB")
    public void setCurrencyTo(Integer currencyCodeB) {
        this.currencyTo = toCurrency(currencyCodeB);
    }

    private static Currency toCurrency(Integer code) {
        return Currency.getAvailableCurrencies().stream()
                .filter(c -> c.getNumericCode() == code)
                .findAny()
                .orElseThrow();
    }
}
