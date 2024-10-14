package org.dharbar.telegabot.service.ticker.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TickerPrice {

    String ticker;
    BigDecimal price;
    LocalDateTime priceUpdatedAt;
}
