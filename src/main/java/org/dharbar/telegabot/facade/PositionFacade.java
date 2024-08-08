package org.dharbar.telegabot.facade;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.controller.request.CreatePositionRequest;
import org.dharbar.telegabot.controller.response.PositionResponse;
import org.dharbar.telegabot.repository.entity.OrderType;
import org.dharbar.telegabot.service.positionmanagment.PositionService;
import org.dharbar.telegabot.service.positionmanagment.dto.OrderDto;
import org.dharbar.telegabot.service.positionmanagment.dto.PositionDto;
import org.dharbar.telegabot.service.positionmanagment.mapper.PositionFacadeMapper;
import org.dharbar.telegabot.service.stockprice.StockPriceService;
import org.dharbar.telegabot.service.stockprice.dto.StockPriceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PositionFacade {
    private final PositionService positionService;
    private final StockPriceService stockPriceService;

    private final PositionFacadeMapper positionFacadeMapper;

    public Page<PositionResponse> getPositions(PageRequest pageRequest) {
        return positionService.getPositions(pageRequest)
                .map(positionFacadeMapper::toResponse)
                .map(this::populateWithAnalytic);
    }

    public Page<PositionResponse> getOpenPositions(PageRequest pageRequest) {
        return positionService.getOpenPositions(pageRequest)
                .map(positionFacadeMapper::toResponse)
                .map(this::populateWithAnalytic);
    }

    public PositionResponse createPosition(CreatePositionRequest request) {
        List<OrderDto> orderDtos = positionFacadeMapper.toDtos(request.getOrders());
        PositionDto positionDto = positionFacadeMapper.toDto(request, orderDtos);

        PositionDto savedPositionDto = positionService.saveNewPosition(positionDto);
        return positionFacadeMapper.toResponse(savedPositionDto);
    }

    @Deprecated
    public PositionResponse saveNewPosition(OrderDto orderDto) {
        PositionDto positionDto = PositionDto.builder()
                .ticker(orderDto.getTicker())
                .orders(List.of(orderDto))
                .build();

        PositionDto savedPositionDto = positionService.saveNewPosition(positionDto);
        return positionFacadeMapper.toResponse(savedPositionDto);
    }

    // TODO (later) for update for position
    @Transactional
    public PositionResponse addPositionNewOrder(UUID positionId, OrderDto order) {
        PositionDto positionDto = positionService.get(positionId);

        List<OrderDto> orders = positionDto.getOrders();
        orders.add(order);

        PositionCalculation positionCalculation = calculatePositionValues(orders);

        positionDto.setNetProfitUsd(positionCalculation.netProfit());
        positionDto.setProfitPercentage(positionCalculation.profitPercentage());
        positionDto.setIsClosed(positionCalculation.isClosed());

        PositionDto savedPosition = positionService.savePosition(positionDto);
        PositionResponse mappedDto = positionFacadeMapper.toResponse(savedPosition);
        return populateWithAnalytic(mappedDto);
    }

    private PositionResponse populateWithAnalytic(PositionResponse positionAnalyticDto) {
        return stockPriceService.find(positionAnalyticDto.getTicker())
                .map(stockPriceDto -> populatePositionWithCurrentValues(positionAnalyticDto, stockPriceDto))
                .orElse(positionAnalyticDto);
    }

    private static PositionResponse populatePositionWithCurrentValues(PositionResponse positionResponse, StockPriceDto stockPriceDto) {
        BigDecimal currentRate = stockPriceDto.getPrice();
        positionResponse.setCurrentRate(currentRate);

        BigDecimal currentPrice = currentRate.multiply(positionResponse.getBuyQuantity());
        BigDecimal netProfitUsd = currentPrice
                .subtract(positionResponse.getBuyTotalUsd())
                .subtract(positionResponse.getBuyCommissionUsd())
                .setScale(3, RoundingMode.HALF_UP);
        positionResponse.setCurrentNetProfitUsd(netProfitUsd);

        BigDecimal profitPercentage = netProfitUsd.divide(positionResponse.getBuyTotalUsd().scaleByPowerOfTen(-2), 3, RoundingMode.HALF_UP);
        positionResponse.setCurrentProfitPercentage(profitPercentage);

        return positionResponse;
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
