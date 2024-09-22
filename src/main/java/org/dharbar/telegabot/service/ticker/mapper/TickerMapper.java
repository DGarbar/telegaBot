package org.dharbar.telegabot.service.ticker.mapper;

import org.dharbar.telegabot.client.ttingo.dto.TiingoQuoteResponse;
import org.dharbar.telegabot.repository.entity.TickerEntity;
import org.dharbar.telegabot.repository.entity.TickerType;
import org.dharbar.telegabot.service.ticker.dto.TickerDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TickerMapper {

    @Mapping(target = "ticker", source = "ticker")
    @Mapping(target = "price", source = "response.close")
    @Mapping(target = "updatedAt", source = "response.date")
    TickerEntity toNewEntity(String ticker, TickerType type, TiingoQuoteResponse response);

    @Mapping(target = "price", source = "response.close")
    @Mapping(target = "updatedAt", source = "response.date")
    @Mapping(target = "ticker", ignore = true)
    TickerEntity toEntity(TiingoQuoteResponse response, @MappingTarget TickerEntity tickerEntity);

    TickerDto toDto(TickerEntity entity);
}
