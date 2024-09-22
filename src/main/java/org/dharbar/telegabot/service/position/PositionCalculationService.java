package org.dharbar.telegabot.service.position;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.repository.entity.OrderType;
import org.dharbar.telegabot.repository.entity.PositionType;
import org.dharbar.telegabot.service.position.dto.OrderDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class PositionCalculationService {

    public static PositionCalculation calculatePositionValues(PositionType type, Collection<OrderDto> orders) {
        BigDecimal buyTotalAmount = BigDecimal.ZERO;
        BigDecimal buyQuantity = BigDecimal.ZERO;
        BigDecimal sellTotalAmount = BigDecimal.ZERO;
        BigDecimal sellQuantity = BigDecimal.ZERO;
        BigDecimal commissionTotalAmount = BigDecimal.ZERO;
        for (OrderDto positionOrder : orders) {
            if (OrderType.BUY == positionOrder.getType()) {
                buyTotalAmount = buyTotalAmount.add(positionOrder.getTotalUsd());
                buyQuantity = buyQuantity.add(positionOrder.getQuantity());
            } else {
                sellTotalAmount = sellTotalAmount.add(positionOrder.getTotalUsd());
                sellQuantity = sellQuantity.add(positionOrder.getQuantity());
            }
            commissionTotalAmount = commissionTotalAmount.add(positionOrder.getCommissionAmount());
        }

        Boolean isClosed = !buyQuantity.equals(BigDecimal.ZERO) && buyQuantity.compareTo(sellQuantity) == 0;

        BigDecimal netProfitAmount = BigDecimal.ZERO;
        BigDecimal profitPercentage = BigDecimal.ZERO;
        if (isClosed) {
            netProfitAmount = sellTotalAmount.subtract(buyTotalAmount).subtract(commissionTotalAmount).setScale(3, RoundingMode.HALF_UP);
            profitPercentage = netProfitAmount.divide(buyTotalAmount.scaleByPowerOfTen(-2), 3, RoundingMode.HALF_UP);
        }

        BigDecimal buyAveragePrice = buyQuantity.equals(BigDecimal.ZERO)
                ? BigDecimal.ZERO
                : buyTotalAmount.divide(buyQuantity, 3, RoundingMode.HALF_UP);
        BigDecimal sellAveragePrice = sellQuantity.equals(BigDecimal.ZERO)
                ? BigDecimal.ZERO
                : sellTotalAmount.divide(sellQuantity, 3, RoundingMode.HALF_UP);

        if (type == PositionType.GRID) {
            isClosed = false;
        }

        return new PositionCalculation(
                buyTotalAmount,
                buyQuantity,
                buyAveragePrice,
                sellTotalAmount,
                sellQuantity,
                sellAveragePrice,
                commissionTotalAmount,
                netProfitAmount,
                profitPercentage,
                isClosed);
    }

    public record PositionCalculation(BigDecimal buyTotalAmount,
                                      BigDecimal buyQuantity,
                                      BigDecimal buyAveragePrice,
                                      BigDecimal sellTotalAmount,
                                      BigDecimal sellQuantity,
                                      BigDecimal sellAveragePrice,
                                      BigDecimal commissionTotalAmount,
                                      BigDecimal netProfitAmount,
                                      BigDecimal profitPercentage,
                                      Boolean isClosed) {
    }
}
