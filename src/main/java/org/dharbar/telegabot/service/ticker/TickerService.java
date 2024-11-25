package org.dharbar.telegabot.service.ticker;

import org.dharbar.telegabot.repository.TickerRepository;
import org.dharbar.telegabot.repository.entity.TickerEntity;
import org.dharbar.telegabot.repository.entity.TickerType;
import org.dharbar.telegabot.service.ticker.dto.TickerDto;
import org.dharbar.telegabot.service.ticker.dto.TickerPrice;
import org.dharbar.telegabot.service.ticker.dto.TickerRangePrice;
import org.dharbar.telegabot.service.ticker.mapper.TickerMapper;
import org.dharbar.telegabot.service.ticker.tickerprice.TickerPriceProvider;
import org.dharbar.telegabot.service.trigger.price.PriceTriggerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TickerService {

    private final PriceTriggerService priceTriggerService;

    private final TickerRepository tickerRepository;

    private final TickerMapper tickerMapper;

    private final Map<TickerType, TickerPriceProvider> typeToProvider;

    public TickerService(PriceTriggerService priceTriggerService,
                         List<TickerPriceProvider> tickerPriceProviders,
                         TickerRepository tickerRepository,
                         TickerMapper tickerMapper) {
        this.priceTriggerService = priceTriggerService;
        this.tickerRepository = tickerRepository;
        this.tickerMapper = tickerMapper;

        typeToProvider = tickerPriceProviders.stream()
                .collect(Collectors.toMap(TickerPriceProvider::getTickerType, provider -> provider));
    }

    public Optional<TickerDto> find(String name) {
        return tickerRepository.findById(name)
                .map(tickerMapper::toDto);
    }

    public Set<TickerDto> getAll() {
        return tickerRepository.findAll().stream()
                .map(tickerMapper::toDto)
                .collect(Collectors.toSet());
    }

    public TickerDto createTicker(String name, TickerType type) {
        TickerPriceProvider tickerPriceProvider = typeToProvider.get(type);
        TickerPrice latestPrice = tickerPriceProvider.getLatestPrice(name);
        TickerEntity ticker = tickerMapper.toNewEntity(name, type, latestPrice);
        TickerEntity savedTicker = tickerRepository.save(ticker);
        return tickerMapper.toDto(savedTicker);
    }

    @Transactional
    public void updateCurrentTickerPricesFromProvider() {
        // Only for crypto for now. Stock has only EOD.
        TickerType crypto = TickerType.CRYPTO;
        TickerPriceProvider stockPriceProvider = typeToProvider.get(crypto);

        List<TickerEntity> cryptoTickers = tickerRepository.findByType(crypto);
        List<String> tickerNames = cryptoTickers.stream().map(TickerEntity::getTicker).toList();
        Map<String, TickerPrice> latestPrices = stockPriceProvider.getLatestPrices(tickerNames);

        cryptoTickers.forEach(ticker -> {
            TickerPrice latestPrice = latestPrices.get(ticker.getTicker());
            TickerEntity updatedEntity = tickerMapper.toEntity(latestPrice, ticker);
            tickerRepository.save(updatedEntity);

            priceTriggerService.checkCurrent(latestPrice);
        });
    }

    // TODO transaction segregation
    @Transactional
    public void updateEndOfDayTickerPricesFromProvider() {
        for (TickerType tickerType : TickerType.values()) {

            TickerPriceProvider stockPriceProvider = typeToProvider.get(tickerType);

            List<TickerEntity> cryptoTickers = tickerRepository.findByType(tickerType);
            List<String> tickerNames = cryptoTickers.stream().map(TickerEntity::getTicker).toList();
            Map<String, TickerRangePrice> dayPrices = stockPriceProvider.getDayPrices(tickerNames);

            cryptoTickers.forEach(ticker -> {
                TickerRangePrice tickerRangePrice = dayPrices.get(ticker.getTicker());
                TickerEntity updatedEntity = tickerMapper.toEntity(tickerRangePrice, ticker);
                tickerRepository.save(updatedEntity);

                priceTriggerService.checkEndOfDay(tickerRangePrice);
            });
        }
    }
}
