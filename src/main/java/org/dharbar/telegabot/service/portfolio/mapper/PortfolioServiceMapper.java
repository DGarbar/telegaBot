package org.dharbar.telegabot.service.portfolio.mapper;

import org.dharbar.telegabot.repository.entity.OrderType;
import org.dharbar.telegabot.repository.entity.PortfolioEntity;
import org.dharbar.telegabot.service.portfolio.dto.PortfolioDto;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED, imports = {OrderType.class})
public interface PortfolioServiceMapper {

    PortfolioDto toDto(PortfolioEntity entity);

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    PortfolioEntity toEntity(PortfolioDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    PortfolioEntity update(@MappingTarget PortfolioEntity portfolioEntity, PortfolioDto dto);
}
