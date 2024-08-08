package org.dharbar.telegabot.csvservice;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("populateStonksDb")
@Service
@RequiredArgsConstructor
public class StonksCsvServiceMapper {

    // private final PositionFacade positionAnalyticFacade;
    // // private final StockPriceService
    //
    // @Transactional
    // public void readStronkFile() throws URISyntaxException, IOException {
    //     Path filePath = Paths.get(ClassLoader.getSystemResource("my/Stonks.csv").toURI());
    //     CsvToBean<StonkRowBean> parser = new CsvToBeanBuilder<StonkRowBean>(new FileReader(filePath.toFile()))
    //             .withType(StonkRowBean.class)
    //             .withSkipLines(1) // Used to skip 1st line. Because columns headers are in 1st line
    //             .build();
    //
    //     List<StonkRowBean> beans = parser.parse().stream()
    //             .filter(stonkRowBean -> StringUtils.isNotBlank(stonkRowBean.getTicker()))
    //             .toList();
    //
    //     beans.forEach(this::toPosition);
    //
    //     System.out.println();
    // }
    //
    // public void toPosition(StonkRowBean stonkRowBean) {
    //     // TODO update stockPrice.
    //
    //     OrderDto buy = OrderDto.builder()
    //             .id(UUID.randomUUID())
    //             .type(OrderType.BUY)
    //             .ticker(stonkRowBean.getTicker())
    //             .dateAt(stonkRowBean.getBuyDateAt())
    //             .quantity(stonkRowBean.getBuyQuantity())
    //             .rate(stonkRowBean.getBuyRate())
    //             .totalUsd(stonkRowBean.getBuyTotalUsd())
    //             .commissionUsd(stonkRowBean.getBuyCommissionUsd())
    //             .build();
    //
    //     PositionResponse positionAnalyticDto = positionAnalyticFacade.saveNewPosition(buy);
    //
    //     boolean isClosed = stonkRowBean.getSellDateAt() != null;
    //     if (isClosed) {
    //         OrderDto sell = OrderDto.builder()
    //                 .id(UUID.randomUUID())
    //                 .type(OrderType.SELL)
    //                 .ticker(stonkRowBean.getTicker())
    //                 .dateAt(stonkRowBean.getSellDateAt())
    //                 .quantity(stonkRowBean.getBuyQuantity())
    //                 .rate(stonkRowBean.getSellRate())
    //                 .totalUsd(stonkRowBean.getBuyQuantity().multiply(stonkRowBean.getSellRate()))
    //                 .commissionUsd(stonkRowBean.getSellCommissionUsd())
    //                 .build();
    //
    //         positionAnalyticFacade.addPositionNewOrder(positionAnalyticDto.getId(), sell);
    //     }
    // }
}
