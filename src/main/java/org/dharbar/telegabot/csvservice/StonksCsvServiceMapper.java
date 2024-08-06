package org.dharbar.telegabot.csvservice;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dharbar.telegabot.csvservice.dto.StonkRowBean;
import org.dharbar.telegabot.repository.entity.OrderType;
import org.dharbar.telegabot.service.positionmanagment.PositionsAnalyticFacade;
import org.dharbar.telegabot.service.positionmanagment.dto.OrderDto;
import org.dharbar.telegabot.service.positionmanagment.dto.PositionAnalyticDto;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Profile("populateStonksDb")
@Service
@RequiredArgsConstructor
public class StonksCsvServiceMapper {

    private final PositionsAnalyticFacade positionAnalyticFacade;
    // private final StockPriceService

    @Transactional
    public void readStronkFile() throws URISyntaxException, IOException {
        Path filePath = Paths.get(ClassLoader.getSystemResource("my/Stonks.csv").toURI());
        CsvToBean<StonkRowBean> parser = new CsvToBeanBuilder<StonkRowBean>(new FileReader(filePath.toFile()))
                .withType(StonkRowBean.class)
                .withSkipLines(1) // Used to skip 1st line. Because columns headers are in 1st line
                .build();

        List<StonkRowBean> beans = parser.parse().stream()
                .filter(stonkRowBean -> StringUtils.isNotBlank(stonkRowBean.getTicker()))
                .toList();

        beans.forEach(this::toPosition);

        System.out.println();
    }

    public void toPosition(StonkRowBean stonkRowBean) {
        // TODO update stockPrice.

        OrderDto buy = OrderDto.builder()
                .id(UUID.randomUUID())
                .type(OrderType.BUY)
                .ticker(stonkRowBean.getTicker())
                .dateAt(stonkRowBean.getBuyDateAt())
                .quantity(stonkRowBean.getBuyQuantity())
                .rate(stonkRowBean.getBuyRate())
                .totalUsd(stonkRowBean.getBuyTotalUsd())
                .commissionUsd(stonkRowBean.getBuyCommissionUsd())
                .build();

        PositionAnalyticDto positionAnalyticDto = positionAnalyticFacade.saveNewPosition(buy);

        boolean isClosed = stonkRowBean.getSellDateAt() != null;
        if (isClosed) {
            OrderDto sell = OrderDto.builder()
                    .id(UUID.randomUUID())
                    .type(OrderType.SELL)
                    .ticker(stonkRowBean.getTicker())
                    .dateAt(stonkRowBean.getSellDateAt())
                    .quantity(stonkRowBean.getBuyQuantity())
                    .rate(stonkRowBean.getSellRate())
                    .totalUsd(stonkRowBean.getBuyQuantity().multiply(stonkRowBean.getSellRate()))
                    .commissionUsd(stonkRowBean.getSellCommissionUsd())
                    .build();

            positionAnalyticFacade.addPositionNewOrder(positionAnalyticDto.getId(), sell);
        }
    }
}
