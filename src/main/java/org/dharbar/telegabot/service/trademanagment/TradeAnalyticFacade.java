package org.dharbar.telegabot.service.trademanagment;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.repository.entity.OrderType;
import org.dharbar.telegabot.service.stockprice.StockPriceService;
import org.dharbar.telegabot.service.stockprice.dto.StockPriceDto;
import org.dharbar.telegabot.service.trademanagment.dto.OrderDto;
import org.dharbar.telegabot.service.trademanagment.dto.TradeAnalyticDto;
import org.dharbar.telegabot.service.trademanagment.dto.TradeDto;
import org.dharbar.telegabot.service.trademanagment.mapper.TradeAnalyticMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TradeAnalyticFacade {
    private final TradeService tradeService;
    private final StockPriceService stockPriceService;

    private final TradeAnalyticMapper tradeAnalyticMapper;

    public TradeAnalyticDto saveNewTrade(OrderDto orderDto) {
        TradeDto tradeDto = tradeService.saveNewTrade(orderDto);
        return tradeAnalyticMapper.toDto(tradeDto);
    }

    // TODO (later) for update for trade
    @Transactional
    public TradeAnalyticDto addTradeNewOrder(UUID tradeId, OrderDto order) {
        TradeDto tradeDto = tradeService.getTrade(tradeId);

        List<OrderDto> orders = tradeDto.getOrders();
        orders.add(order);

        TradeCalculation tradeCalculation = calculateTradeValues(orders);

        tradeDto.setNetProfitUsd(tradeCalculation.netProfit());
        tradeDto.setProfitPercentage(tradeCalculation.profitPercentage());
        tradeDto.setIsClosed(tradeCalculation.isClosed());

        TradeDto savedTrade = tradeService.saveTrade(tradeDto);
        TradeAnalyticDto mappedDto = tradeAnalyticMapper.toDto(savedTrade);
        return populateWithAnalytic(mappedDto);
    }

    public List<TradeAnalyticDto> getOpenTrades(PageRequest pageRequest) {
        return tradeService.getOpenTrades(pageRequest).stream()
                .map(tradeAnalyticMapper::toDto)
                .map(this::populateWithAnalytic)
                .toList();
    }

    public List<TradeAnalyticDto> getTrades(PageRequest pageRequest) {
        return tradeService.getTrades(pageRequest).stream()
                .map(tradeAnalyticMapper::toDto)
                .map(this::populateWithAnalytic)
                .toList();
    }

    private TradeAnalyticDto populateWithAnalytic(TradeAnalyticDto tradeAnalyticDto) {
        return stockPriceService.find(tradeAnalyticDto.getTicker())
                .map(stockPriceDto -> populateTradeWithCurrentValues(tradeAnalyticDto, stockPriceDto))
                .orElse(tradeAnalyticDto);
    }

    private static TradeAnalyticDto populateTradeWithCurrentValues(TradeAnalyticDto tradeAnalyticDto, StockPriceDto stockPriceDto) {
        BigDecimal currentRate = stockPriceDto.getPrice();
        tradeAnalyticDto.setCurrentRate(currentRate);

        BigDecimal currentPrice = currentRate.multiply(tradeAnalyticDto.getBuyQuantity());
        BigDecimal netProfitUsd = currentPrice
                .subtract(tradeAnalyticDto.getBuyTotalUsd())
                .subtract(tradeAnalyticDto.getBuyCommissionUsd())
                .setScale(3, RoundingMode.HALF_UP);
        tradeAnalyticDto.setCurrentNetProfitUsd(netProfitUsd);

        BigDecimal profitPercentage = netProfitUsd.divide(tradeAnalyticDto.getBuyTotalUsd().scaleByPowerOfTen(-2), 3, RoundingMode.HALF_UP);
        tradeAnalyticDto.setCurrentProfitPercentage(profitPercentage);

        return tradeAnalyticDto;
    }

    public StockPriceDto createStockPrice(String ticker) {
        return stockPriceService.createStockPrice(ticker);
    }

    private static TradeCalculation calculateTradeValues(List<OrderDto> orders) {
        BigDecimal buyTotal = BigDecimal.ZERO;
        BigDecimal buyQuantity = BigDecimal.ZERO;
        BigDecimal sellTotal = BigDecimal.ZERO;
        BigDecimal sellQuantity = BigDecimal.ZERO;
        BigDecimal commissionTotal = BigDecimal.ZERO;
        for (OrderDto tradeOrder : orders) {
            if (OrderType.BUY == tradeOrder.getType()) {
                buyTotal = buyTotal.add(tradeOrder.getTotalUsd());
                buyQuantity = buyQuantity.add(tradeOrder.getQuantity());
            } else {
                sellTotal = sellTotal.add(tradeOrder.getTotalUsd());
                sellQuantity = sellQuantity.add(tradeOrder.getQuantity());
            }
            commissionTotal = commissionTotal.add(tradeOrder.getCommissionUsd());
        }

        BigDecimal netProfit = sellTotal.subtract(buyTotal).subtract(commissionTotal).setScale(3, RoundingMode.HALF_UP);
        BigDecimal profitPercentage = netProfit.divide(buyTotal.scaleByPowerOfTen(-2), 3, RoundingMode.HALF_UP);
        Boolean isClosed = buyQuantity.compareTo(sellQuantity) == 0;
        return new TradeCalculation(buyTotal, buyQuantity, sellTotal, sellQuantity, commissionTotal, netProfit, profitPercentage, isClosed);
    }

    private record TradeCalculation(BigDecimal buyTotal,
                                    BigDecimal buyQuantity,
                                    BigDecimal sellTotal,
                                    BigDecimal sellQuantity,
                                    BigDecimal commissionTotal,
                                    BigDecimal netProfit,
                                    BigDecimal profitPercentage,
                                    Boolean isClosed) {
    }
}
