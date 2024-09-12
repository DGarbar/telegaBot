package org.dharbar.telegabot.facade;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.controller.filter.PositionFilter;
import org.dharbar.telegabot.controller.request.CreateOrderRequest;
import org.dharbar.telegabot.controller.request.CreatePositionRequest;
import org.dharbar.telegabot.controller.request.UpdatePositionRequest;
import org.dharbar.telegabot.controller.response.PositionResponse;
import org.dharbar.telegabot.facade.mapper.PositionFacadeMapper;
import org.dharbar.telegabot.service.position.PositionService;
import org.dharbar.telegabot.service.position.dto.OrderDto;
import org.dharbar.telegabot.service.position.dto.PositionDto;
import org.dharbar.telegabot.service.position.dto.PriceTriggerDto;
import org.dharbar.telegabot.service.stockprice.StockPriceService;
import org.dharbar.telegabot.service.stockprice.dto.StockPriceDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PositionFacade {
    private final PositionService positionService;
    private final StockPriceService stockPriceService;

    private final PositionFacadeMapper positionFacadeMapper;

    @Transactional(readOnly = true)
    public Page<PositionResponse> getPositions(PositionFilter filter, Pageable pageRequest) {
        return positionService.getPositions(filter, pageRequest)
                .map(positionFacadeMapper::toResponse)
                .map(this::populateWithAnalytic);
    }

    @Transactional
    public PositionResponse createPosition(CreatePositionRequest request) {
        Set<OrderDto> orderDtos = positionFacadeMapper.toDtoOrders(request.getOrders());
        Set<PriceTriggerDto> priceTriggerDtos = positionFacadeMapper.toDtoPriceTriggers(request.getPriceTriggers());
        PositionDto savedPositionDto = positionService.cretePosition(
                request.getTicker(),
                request.getPortfolioId(),
                request.getComment(),
                orderDtos,
                priceTriggerDtos);
        PositionResponse response = positionFacadeMapper.toResponse(savedPositionDto);
        return populateWithAnalytic(response);
    }

    @Transactional
    public PositionResponse updatePosition(UUID id, UpdatePositionRequest request) {
        Set<OrderDto> orderDtos = positionFacadeMapper.toDtoUpdateOrders(request.getOrders());
        Set<PriceTriggerDto> priceTriggerDtos = positionFacadeMapper.toDtoUpdatePriceTriggers(request.getPriceTriggers());
        PositionDto positionDto = positionFacadeMapper.toDto(id, request, orderDtos, priceTriggerDtos);

        PositionDto savedPositionDto = positionService.updatePosition(positionDto);
        PositionResponse response = positionFacadeMapper.toResponse(savedPositionDto);
        return populateWithAnalytic(response);
    }

    @Transactional
    public PositionResponse addPositionNewOrder(UUID positionId, CreateOrderRequest order) {
        OrderDto orderDto = positionFacadeMapper.toDto(order);
        PositionDto positionDto = positionService.addOrder(positionId, orderDto);
        PositionResponse mappedDto = positionFacadeMapper.toResponse(positionDto);
        return populateWithAnalytic(mappedDto);
    }

    @Transactional
    public PositionResponse updatePositionOrder(UUID positionId, UUID orderId, CreateOrderRequest request) {
        OrderDto orderDto = positionFacadeMapper.toDto(orderId, request);
        PositionDto positionDto = positionService.updatePositionOrder(positionId, orderDto);
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
        if (positionResponse.getIsClosed() || positionResponse.getOrders().isEmpty()) {
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

        BigDecimal buyTotalAmount = positionResponse.getBuyTotalAmount().scaleByPowerOfTen(-2);
        BigDecimal currentProfitPercentage = currentNetProfitAmount.divide(buyTotalAmount, 3, RoundingMode.HALF_UP);
        positionResponse.setCurrentProfitPercentage(currentProfitPercentage);

        return positionResponse;
    }

    @Transactional
    public void deletePositionAlarm(UUID positionId, UUID alarmId) {
        positionService.deleteAlarm(alarmId, positionId);
    }
}
