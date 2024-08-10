package org.dharbar.telegabot.service.positionmanagment.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PositionDto {

    UUID id;

    String ticker;
    LocalDate openAt;
    LocalDate closedAt;

    List<OrderDto> orders;

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
