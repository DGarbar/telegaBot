package org.dharbar.telegabot.service.ticker.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TickerRangePrice {

    String ticker;

    BigDecimal firstPrice;
    BigDecimal lastPrice;

    BigDecimal highPrice;
    BigDecimal lowPrice;


    LocalDateTime firstAt;
    LocalDateTime lastAt;
}
