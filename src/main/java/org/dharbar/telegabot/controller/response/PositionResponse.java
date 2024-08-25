package org.dharbar.telegabot.controller.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PositionResponse {

    UUID id;

    String ticker;
    LocalDate openAt;
    LocalDate closedAt;

    UUID portfolioId;

    List<OrderResponse> orders;

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
