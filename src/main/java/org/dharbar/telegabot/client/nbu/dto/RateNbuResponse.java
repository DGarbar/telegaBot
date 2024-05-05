package org.dharbar.telegabot.client.nbu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

import java.util.Currency;

@Data
public class RateNbuResponse {

    private String txt;

    private double rate;

    private Currency currency;

    @JsonProperty("exchangedate")
    private String exchangeDate;

    @JsonSetter("cc")
    public void setCurrency(String currencyCode) {
        this.currency = Currency.getInstance(currencyCode);
    }
}
