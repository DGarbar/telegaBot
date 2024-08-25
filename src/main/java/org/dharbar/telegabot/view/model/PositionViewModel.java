package org.dharbar.telegabot.view.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.DAYS;

@Data
@Builder
public class PositionViewModel {

    UUID id;

    String ticker;

    UUID portfolioId;

    LocalDate openAt;
    LocalDate closedAt;

    List<OrderViewModel> orders;

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

    public String getViewSellRate() {
        return BigDecimal.ZERO.equals(sellAveragePrice) ? "*" + currentRatePrice : sellAveragePrice.toString();
    }

    public String getViewNetProfitAmount() {
        return !isClosed && BigDecimal.ZERO.equals(netProfitAmount) ? "*" + currentNetProfitAmount : netProfitAmount.toString();
    }

    public String getViewProfitPercentage() {
        return !isClosed && BigDecimal.ZERO.equals(profitPercentage)  ? "*" + currentProfitPercentage : profitPercentage.toString();
    }

    public long dealDurationDays() {
        LocalDate positionEnd = closedAt == null ? LocalDate.now() : closedAt;
        return DAYS.between(openAt, positionEnd);
    }
}
