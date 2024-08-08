package org.dharbar.telegabot.controller.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.DAYS;

@Data
@Builder
public class PositionResponse {

    UUID id;

    String ticker;
    LocalDate dateAt;

    // TODO make it list
    OrderResponse byuOrder;
    OrderResponse sellOrder;

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

    public long dealDurationDays() {
        LocalDate sellDate = sellOrder == null ? LocalDate.now() : sellOrder.getDateAt();
        return DAYS.between(byuOrder.getDateAt(), sellDate);
    }
}
