package org.dharbar.telegabot.service.positionmanagment;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.repository.entity.OrderType;
import org.dharbar.telegabot.service.positionmanagment.dto.OrderDto;
import org.dharbar.telegabot.service.positionmanagment.dto.PositionAnalyticDto;
import org.dharbar.telegabot.service.positionmanagment.dto.PositionDto;
import org.dharbar.telegabot.service.positionmanagment.mapper.PositionAnalyticMapper;
import org.dharbar.telegabot.service.stockprice.StockPriceService;
import org.dharbar.telegabot.service.stockprice.dto.StockPriceDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PositionsAnalyticFacade {
    private final PositionService positionService;
    private final StockPriceService stockPriceService;

    private final PositionAnalyticMapper positionAnalyticMapper;

    public PositionAnalyticDto saveNewPosition(OrderDto orderDto) {
        PositionDto positionDto = positionService.saveNewPosition(orderDto);
        return positionAnalyticMapper.toDto(positionDto);
    }

    // TODO (later) for update for position
    @Transactional
    public PositionAnalyticDto addPositionNewOrder(UUID positionId, OrderDto order) {
        PositionDto positionDto = positionService.getPosition(positionId);

        List<OrderDto> orders = positionDto.getOrders();
        orders.add(order);

        PositionCalculation positionCalculation = calculatePositionValues(orders);

        positionDto.setNetProfitUsd(positionCalculation.netProfit());
        positionDto.setProfitPercentage(positionCalculation.profitPercentage());
        positionDto.setIsClosed(positionCalculation.isClosed());

        PositionDto savedPosition = positionService.savePosition(positionDto);
        PositionAnalyticDto mappedDto = positionAnalyticMapper.toDto(savedPosition);
        return populateWithAnalytic(mappedDto);
    }

    public List<PositionAnalyticDto> getOpenPositions(PageRequest pageRequest) {
        return positionService.getOpenPositions(pageRequest).stream()
                .map(positionAnalyticMapper::toDto)
                .map(this::populateWithAnalytic)
                .toList();
    }

    public List<PositionAnalyticDto> getPositions(PageRequest pageRequest) {
        return positionService.getPositions(pageRequest).stream()
                .map(positionAnalyticMapper::toDto)
                .map(this::populateWithAnalytic)
                .toList();
    }

    private PositionAnalyticDto populateWithAnalytic(PositionAnalyticDto positionAnalyticDto) {
        return stockPriceService.find(positionAnalyticDto.getTicker())
                .map(stockPriceDto -> populatePositionWithCurrentValues(positionAnalyticDto, stockPriceDto))
                .orElse(positionAnalyticDto);
    }

    private static PositionAnalyticDto populatePositionWithCurrentValues(PositionAnalyticDto positionAnalyticDto, StockPriceDto stockPriceDto) {
        BigDecimal currentRate = stockPriceDto.getPrice();
        positionAnalyticDto.setCurrentRate(currentRate);

        BigDecimal currentPrice = currentRate.multiply(positionAnalyticDto.getBuyQuantity());
        BigDecimal netProfitUsd = currentPrice
                .subtract(positionAnalyticDto.getBuyTotalUsd())
                .subtract(positionAnalyticDto.getBuyCommissionUsd())
                .setScale(3, RoundingMode.HALF_UP);
        positionAnalyticDto.setCurrentNetProfitUsd(netProfitUsd);

        BigDecimal profitPercentage = netProfitUsd.divide(positionAnalyticDto.getBuyTotalUsd().scaleByPowerOfTen(-2), 3, RoundingMode.HALF_UP);
        positionAnalyticDto.setCurrentProfitPercentage(profitPercentage);

        return positionAnalyticDto;
    }

    public StockPriceDto createStockPrice(String ticker) {
        return stockPriceService.createStockPrice(ticker);
    }

    private static PositionCalculation calculatePositionValues(List<OrderDto> orders) {
        BigDecimal buyTotal = BigDecimal.ZERO;
        BigDecimal buyQuantity = BigDecimal.ZERO;
        BigDecimal sellTotal = BigDecimal.ZERO;
        BigDecimal sellQuantity = BigDecimal.ZERO;
        BigDecimal commissionTotal = BigDecimal.ZERO;
        for (OrderDto positionOrder : orders) {
            if (OrderType.BUY == positionOrder.getType()) {
                buyTotal = buyTotal.add(positionOrder.getTotalUsd());
                buyQuantity = buyQuantity.add(positionOrder.getQuantity());
            } else {
                sellTotal = sellTotal.add(positionOrder.getTotalUsd());
                sellQuantity = sellQuantity.add(positionOrder.getQuantity());
            }
            commissionTotal = commissionTotal.add(positionOrder.getCommissionUsd());
        }

        BigDecimal netProfit = sellTotal.subtract(buyTotal).subtract(commissionTotal).setScale(3, RoundingMode.HALF_UP);
        BigDecimal profitPercentage = netProfit.divide(buyTotal.scaleByPowerOfTen(-2), 3, RoundingMode.HALF_UP);
        Boolean isClosed = buyQuantity.compareTo(sellQuantity) == 0;
        return new PositionCalculation(buyTotal, buyQuantity, sellTotal, sellQuantity, commissionTotal, netProfit, profitPercentage, isClosed);
    }

    private record PositionCalculation(BigDecimal buyTotal,
                                    BigDecimal buyQuantity,
                                    BigDecimal sellTotal,
                                    BigDecimal sellQuantity,
                                    BigDecimal commissionTotal,
                                    BigDecimal netProfit,
                                    BigDecimal profitPercentage,
                                    Boolean isClosed) {
    }
}
