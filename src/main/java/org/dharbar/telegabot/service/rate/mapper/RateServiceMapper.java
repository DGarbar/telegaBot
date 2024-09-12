package org.dharbar.telegabot.service.rate.mapper;

import org.dharbar.telegabot.client.binance.response.BinanceP2pResult;
import org.dharbar.telegabot.client.mono.response.RateMonoResponse;
import org.dharbar.telegabot.client.nbu.dto.RateNbuResponse;
import org.dharbar.telegabot.repository.entity.OrderType;
import org.dharbar.telegabot.service.rate.dto.RateDto;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED, imports = {OrderType.class})
public interface RateServiceMapper {

    @Mapping(target = "currencyFrom", expression = "java(java.util.Currency.getInstance(\"USD\"))")
    @Mapping(target = "currencyTo", expression = "java(java.util.Currency.getInstance(advertise.getFiatUnit()))")
    @Mapping(target = "rateSell", source = "advertise.price")
    @Mapping(target = "amount", source = "advertise.maxSingleTransAmount")
    @Mapping(target = "rateProvider", constant = "BINANCE")
    @Mapping(target = "rateBuy", ignore = true)
    RateDto toDto(BinanceP2pResult.Advertise advertise);

    @Mapping(target = "rateProvider", constant = "MONO")
    @Mapping(target = "amount", ignore = true)
    RateDto toDto(RateMonoResponse response);

    @Mapping(target = "currencyFrom", source = "currency")
    @Mapping(target = "currencyTo", expression = "java(java.util.Currency.getInstance(\"UAH\"))")
    @Mapping(target = "rateBuy", source = "rate")
    @Mapping(target = "rateSell", ignore = true)
    @Mapping(target = "amount", ignore = true)
    @Mapping(target = "rateProvider", constant = "NBU")
    RateDto toDto(RateNbuResponse response);
}
