package org.dharbar.telegabot.service.trademanagment.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class TradeDto {

    UUID id;

    String ticker;
    LocalDate dateAt;

    List<OrderDto> orders;

    Boolean isClosed;

    BigDecimal netProfitUsd;
    BigDecimal profitPercentage;
    String comment;
}
