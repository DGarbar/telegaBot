package org.dharbar.telegabot.facade;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.controller.request.CreateOrderRequest;
import org.dharbar.telegabot.controller.request.CreatePositionRequest;
import org.dharbar.telegabot.controller.response.PositionResponse;
import org.dharbar.telegabot.facade.mapper.PositionFacadeMapper;
import org.dharbar.telegabot.service.positionmanagment.PositionService;
import org.dharbar.telegabot.service.positionmanagment.dto.OrderDto;
import org.dharbar.telegabot.service.positionmanagment.dto.PositionDto;
import org.dharbar.telegabot.service.stockprice.StockPriceService;
import org.dharbar.telegabot.service.stockprice.dto.StockPriceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<PositionResponse> getPositions(Pageable pageRequest) {
        return positionService.getPositions(pageRequest)
                .map(positionFacadeMapper::toResponse)
                .map(this::populateWithAnalytic);
    }

    public Page<PositionResponse> getOpenPositions(Pageable pageRequest) {
        return positionService.getOpenPositions(pageRequest)
                .map(positionFacadeMapper::toResponse)
                .map(this::populateWithAnalytic);
    }

    @Transactional
    public PositionResponse createPosition(CreatePositionRequest request) {
        List<OrderDto> orderDtos = positionFacadeMapper.toDtos(request.getOrders());
        PositionDto savedPositionDto = positionService.cretePosition(request.getTicker(), request.getComment(), orderDtos);
        PositionResponse response = positionFacadeMapper.toResponse(savedPositionDto);
        return populateWithAnalytic(response);
    }

    // TODO (later) for update for position
    @Transactional
    public PositionResponse addPositionNewOrder(UUID positionId, CreateOrderRequest order) {
        OrderDto orderDto = positionFacadeMapper.toDto(order);
        PositionDto positionDto = positionService.addOrder(positionId, orderDto);
        PositionResponse mappedDto = positionFacadeMapper.toResponse(positionDto);
        return populateWithAnalytic(mappedDto);
    }

    private PositionResponse populateWithAnalytic(PositionResponse positionAnalyticDto) {
        return stockPriceService.find(positionAnalyticDto.getTicker())
                .map(stockPriceDto -> populatePositionWithCurrentValues(positionAnalyticDto, stockPriceDto))
                .orElse(positionAnalyticDto);
    }

    private static PositionResponse populatePositionWithCurrentValues(PositionResponse positionResponse, StockPriceDto stockPriceDto) {
        BigDecimal currentRate = stockPriceDto.getPrice();
        positionResponse.setCurrentRatePrice(currentRate);
        if (positionResponse.getIsClosed()) {
            positionResponse.setCurrentNetProfitAmount(BigDecimal.ZERO);
            positionResponse.setCurrentProfitPercentage(BigDecimal.ZERO);
            return positionResponse;
        }

        BigDecimal leftQuantity = positionResponse.getBuyQuantity().subtract(positionResponse.getSellQuantity());
        BigDecimal currentLeftPrice = leftQuantity.multiply(currentRate);

        BigDecimal currentNetProfitAmount = currentLeftPrice
                .subtract(leftQuantity.multiply(positionResponse.getBuyAveragePrice()))
                .add(positionResponse.getSellTotalAmount())
                .subtract(positionResponse.getCommissionTotalAmount())
                .setScale(3, RoundingMode.HALF_UP);
        positionResponse.setCurrentNetProfitAmount(currentNetProfitAmount);

        BigDecimal currentProfitPercentage = currentNetProfitAmount.divide(positionResponse.getBuyTotalAmount().scaleByPowerOfTen(-2), 3, RoundingMode.HALF_UP);
        positionResponse.setCurrentProfitPercentage(currentProfitPercentage);

        return positionResponse;
    }


}