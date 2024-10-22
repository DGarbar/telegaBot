package org.dharbar.telegabot.view.model;

import lombok.Builder;
import lombok.Data;
import org.dharbar.telegabot.repository.entity.PositionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class PositionViewModel {

    UUID id;

    String name;

    String ticker;

    PositionType type;

    UUID portfolioId;

    LocalDate openAt;
    LocalDate closedAt;

    Set<OrderViewModel> orders;
    Set<PriceTriggerViewModel> priceTriggers;
    Set<AlarmViewModel> alarms;

    // Calculated fields
    Boolean isClosed;

    BigDecimal buyTotalAmount;
    BigDecimal buyQuantity;
    BigDecimal buyAveragePrice;

    BigDecimal sellTotalAmount;
    BigDecimal sellQuantity;
    BigDecimal sellAveragePrice;

    BigDecimal investedAmount;

    BigDecimal commissionTotalAmount;

    BigDecimal netProfitAmount;
    BigDecimal profitPercentage;

    String comment;

    BigDecimal currentRatePrice;
    BigDecimal currentNetProfitAmount;
    BigDecimal currentProfitPercentage;

    // public String getViewSellRate() {
    //     return BigDecimal.ZERO.equals(sellAveragePrice) ? "*" + currentRatePrice : sellAveragePrice.toString();
    // }
    //
    // public String getViewNetProfitAmount() {
    //     return !isClosed && BigDecimal.ZERO.equals(netProfitAmount) ? "*" + currentNetProfitAmount : netProfitAmount.toString();
    // }
    //
    // public String getViewProfitPercentage() {
    //     return !isClosed && BigDecimal.ZERO.equals(profitPercentage)  ? "*" + currentProfitPercentage : profitPercentage.toString();
    // }
    //
    // public long dealDurationDays() {
    //     LocalDate positionEnd = closedAt == null ? LocalDate.now() : closedAt;
    //     return DAYS.between(openAt, positionEnd);
    // }
}
