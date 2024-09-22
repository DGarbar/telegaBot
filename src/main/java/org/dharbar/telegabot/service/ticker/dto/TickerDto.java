package org.dharbar.telegabot.service.ticker.dto;

import lombok.Builder;
import lombok.Data;
import org.dharbar.telegabot.repository.entity.TickerType;

import java.math.BigDecimal;

@Data
@Builder
public class TickerDto {

    String ticker;
    BigDecimal price;
    TickerType type;

    BigDecimal emaDay200Price;

    BigDecimal priceBuy;
    BigDecimal priceSell;
}
