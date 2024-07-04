package org.dharbar.telegabot.service.trademanagment.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class TradeAnalyticDto {

    UUID id;

    String ticker;
    LocalDate dateAt;

    OrderDto byuOrder;
    OrderDto sellOrder;

    Boolean isClosed;

    BigDecimal netProfitUsd;
    BigDecimal profitPercentage;
    String comment;

    BigDecimal currentRate;
    BigDecimal currentNetProfitUsd;
    BigDecimal currentProfitPercentage;

    public BigDecimal getBuyQuantity() {
        return byuOrder.getQuantity();
    }

    public BigDecimal getBuyTotalUsd() {
        return byuOrder.getTotalUsd();
    }

    public BigDecimal getBuyCommissionUsd() {
        return byuOrder.getCommissionUsd();
    }

    public BigDecimal getBuyRate() {
        return byuOrder.getRate();
    }

    public BigDecimal getSellRate() {
        return sellOrder == null ? null : sellOrder.getRate();
    }

    public String getViewSellRate() {
        return getSellRate() == null ? "*" + currentRate : getSellRate().toString();
    }

    public String getViewNetProfitUsd() {
        return netProfitUsd == null ? "*" + currentNetProfitUsd : netProfitUsd.toString();
    }

    public String getViewProfitPercentage() {
        return profitPercentage == null ? "*" + currentProfitPercentage : profitPercentage.toString();
    }
}
