package org.dharbar.telegabot.service.stockprice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class StockPriceDto {

    String ticker;
    BigDecimal price;
}
