package org.dharbar.telegabot.service.trademanagment.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class TradeDto {

    UUID id;

    OrderDto byuOrder;

    @Builder.Default
    Set<OrderDto> sellOrders = new HashSet<>();

    Boolean isClosed;

    BigDecimal netProfitUsd;
    BigDecimal profitPercentage;
    String comment;

    public String getTicker() {
        return byuOrder.getTicker();
    }

    public LocalDate getBuyDateAt() {
        return byuOrder.getDateAt();
    }

    public BigDecimal getBuyQuantity() {
        return byuOrder.getQuantity();
    }

    public BigDecimal getBuyTotalUsd() {
        return byuOrder.getTotalUsd();
    }

    public BigDecimal getBuyRate() {
        return byuOrder.getTotalUsd();
    }
}