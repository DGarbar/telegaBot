package org.dharbar.telegabot.service.trademanagment.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

// TODO
@Data
@Builder
public class TradeAnalysisDto {

    UUID id;

    String ticker;
    LocalDate dateAt;

    OrderDto byuOrder;
    OrderDto sellOrder;

    Boolean isClosed;

    BigDecimal netProfitUsd;
    BigDecimal profitPercentage;
    String comment;

    public BigDecimal getBuyQuantity() {
        return byuOrder.getQuantity();
    }

    public BigDecimal getBuyTotalUsd() {
        return byuOrder.getTotalUsd();
    }

    public BigDecimal getBuyRate() {
        return byuOrder.getTotalUsd();
    }

    public BigDecimal getSellRate() {
        return sellOrder == null ? null : sellOrder.getRate();
    }
}
