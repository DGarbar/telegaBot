package org.dharbar.telegabot.controller.response;

import lombok.Builder;
import lombok.Data;
import org.dharbar.telegabot.repository.entity.PositionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class PositionResponse {

    UUID id;

    String name;
    String ticker;
    PositionType type;
    LocalDate openAt;
    LocalDate closedAt;

    UUID portfolioId;

    Set<OrderResponse> orders;
    Set<PriceTriggerResponse> priceTriggers;
    Set<AlarmResponse> alarms;

    Boolean isClosed;

    BigDecimal buyTotalAmount;
    BigDecimal buyQuantity;
    BigDecimal buyAveragePrice;

    BigDecimal sellTotalAmount;
    BigDecimal sellQuantity;
    BigDecimal sellAveragePrice;

    BigDecimal commissionTotalAmount;

    BigDecimal netProfitAmount;
    BigDecimal profitPercentage;

    String comment;

    BigDecimal currentRatePrice;
    BigDecimal currentNetProfitAmount;
    BigDecimal currentProfitPercentage;
}
