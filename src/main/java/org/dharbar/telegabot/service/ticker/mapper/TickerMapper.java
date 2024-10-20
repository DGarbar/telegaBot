package org.dharbar.telegabot.service.ticker.mapper;

import org.dharbar.telegabot.repository.entity.TickerEntity;
import org.dharbar.telegabot.repository.entity.TickerType;
import org.dharbar.telegabot.service.ticker.dto.TickerDto;
import org.dharbar.telegabot.service.ticker.dto.TickerPrice;
import org.dharbar.telegabot.service.ticker.dto.TickerRangePrice;
import org.dharbar.telegabot.utils.mapper.StripBigDecimalMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {StripBigDecimalMapper.class})
public interface TickerMapper {

    @Mapping(target = "ticker", source = "ticker")
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "priceSell", ignore = true)
    @Mapping(target = "priceBuy", ignore = true)
    @Mapping(target = "emaDay200Price", ignore = true)
    TickerEntity toNewEntity(String ticker, TickerType type, TickerPrice price);

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "priceSell", ignore = true)
    @Mapping(target = "priceBuy", ignore = true)
    @Mapping(target = "emaDay200Price", ignore = true)
    @Mapping(target = "ticker", ignore = true)
    TickerEntity toEntity(TickerPrice price, @MappingTarget TickerEntity tickerEntity);

    @Mapping(target = "price", source = "lastPrice")
    @Mapping(target = "priceUpdatedAt", source = "lastAt")
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "priceSell", ignore = true)
    @Mapping(target = "priceBuy", ignore = true)
    @Mapping(target = "emaDay200Price", ignore = true)
    @Mapping(target = "ticker", ignore = true)
    TickerEntity toEntity(TickerRangePrice price, @MappingTarget TickerEntity tickerEntity);

    TickerDto toDto(TickerEntity entity);

}
