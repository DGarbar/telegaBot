package org.dharbar.telegabot.service.ticker;

import org.dharbar.telegabot.repository.TickerRepository;
import org.dharbar.telegabot.repository.entity.TickerEntity;
import org.dharbar.telegabot.repository.entity.TickerType;
import org.dharbar.telegabot.service.pricetrigger.PriceTriggerService;
import org.dharbar.telegabot.service.ticker.dto.TickerDto;
import org.dharbar.telegabot.service.ticker.dto.TickerPrice;
import org.dharbar.telegabot.service.ticker.mapper.TickerMapper;
import org.dharbar.telegabot.service.ticker.tickerprice.TickerPriceProvider;
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
        TickerPriceProvider stockPriceProvider = typeToProvider.get(TickerType.STOCK);

        List<TickerEntity> cryptoTickers = tickerRepository.findByType(TickerType.CRYPTO);
        List<String> tickerNames = cryptoTickers.stream().map(TickerEntity::getTicker).toList();
        Map<String, TickerPrice> latestPrices = stockPriceProvider.getLatestPrices(tickerNames);

        cryptoTickers.forEach(ticker -> {
            TickerPrice latestPrice = latestPrices.get(ticker.getTicker());
            TickerEntity updatedEntity = tickerMapper.toEntity(latestPrice, ticker);
            tickerRepository.save(updatedEntity);

            priceTriggerService.checkCurrent(latestPrice);
        });
    }

    @Transactional
    public void updateEndOfDayTickerPricesFromProvider() {
        // tickerRepository.findAll()
        //         .forEach(this::updateTickerPriceFromProvider);
    }

    // TODO in new transaction ?
    private void updateTickerPriceFromProvider(TickerEntity tickerEntity) {
        TickerPriceProvider tickerPriceProvider = typeToProvider.get(tickerEntity.getType());
        TickerPrice latestPrice = tickerPriceProvider.getLatestPrice(tickerEntity.getTicker());

        TickerEntity updatedEntity = tickerMapper.toEntity(latestPrice, tickerEntity);
        tickerRepository.save(updatedEntity);

        // priceTriggerService.checkEndOfDay(tickerName, response.getLow(), response.getHigh(), updatedEntity.getPriceUpdatedAt());
    }
}
