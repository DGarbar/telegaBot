package org.dharbar.telegabot.client.rate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.dharbar.telegabot.service.dto.Currency;

import java.math.BigDecimal;

@Data
public class RateResponse {

    private String txt;

    private BigDecimal rate;

    @JsonProperty("cc")
    private Currency currency;

    @JsonProperty("exchangedate")
    private String exchangeDate;
}
