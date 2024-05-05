package org.dharbar.telegabot.service.dto;

import lombok.Value;

import java.math.BigDecimal;

@Value
public class RateDto {
    Currency currency;
    BigDecimal value;
}
