package org.dharbar.telegabot.service.stockprice.mapper;

import org.dharbar.telegabot.client.ttingo.dto.TiingoQuoteResponse;
import org.dharbar.telegabot.repository.entity.StockPriceEntity;
import org.dharbar.telegabot.service.stockprice.dto.StockPriceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StockPriceMapper {

    @Mapping(target = "ticker", source = "ticker")
    @Mapping(target = "price", source = "response.close")
    @Mapping(target = "updatedAt", source = "response.date")
    StockPriceEntity toNewEntity(String ticker, TiingoQuoteResponse response);

    @Mapping(target = "price", source = "response.close")
    @Mapping(target = "updatedAt", source = "response.date")
    @Mapping(target = "ticker", ignore = true)
    StockPriceEntity toEntity(TiingoQuoteResponse response, @MappingTarget StockPriceEntity stockPriceEntity);

    StockPriceDto toDto(StockPriceEntity entity);
}
