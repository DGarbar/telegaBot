package org.dharbar.telegabot.service.position.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class PositionDto {

    UUID id;

    String ticker;

    UUID portfolioId;

    LocalDate openAt;
    LocalDate closedAt;

    Set<OrderDto> orders;

    Set<PriceTriggerDto> priceTriggers;

    Set<AlarmDto> alarms;

    Boolean isClosed;

    BigDecimal buyTotalAmount;
    BigDecimal buyQuantity;
    BigDecimal buyAveragePrice;

    BigDecimal sellTotalAmount;
    BigDecimal sellQuantity;
    BigDecimal sellAveragePrice;

    BigDecimal commissionTotalAmount;

    // Only for closed position
    BigDecimal netProfitAmount;
    BigDecimal profitPercentage;

    String comment;
}
