package org.dharbar.telegabot.service.ticker;

import jakarta.ws.rs.NotSupportedException;
import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.client.ttingo.TiingoClient;
import org.dharbar.telegabot.client.ttingo.dto.TiingoQuoteResponse;
import org.dharbar.telegabot.repository.TickerRepository;
import org.dharbar.telegabot.repository.entity.TickerEntity;
import org.dharbar.telegabot.repository.entity.TickerType;
import org.dharbar.telegabot.service.ticker.dto.TickerDto;
import org.dharbar.telegabot.service.ticker.mapper.TickerMapper;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TickerService {

    private final TiingoClient exchangeQuoteProvider;
    private final TickerRepository tickerRepository;

    private final TickerMapper tickerMapper;

    @Cacheable("ticker")
    public Optional<TickerDto> find(String name) {
        return tickerRepository.findById(name)
                .map(tickerMapper::toDto);
    }

    public Set<TickerDto> getAll() {
        return tickerRepository.findAll().stream()
                .map(tickerMapper::toDto)
                .collect(Collectors.toSet());
    }

    // TODO maybe ignore that has same EOD
    public void updateEndOfDayTickerPricesFromProvider() {
        tickerRepository.findAll()
                .forEach(this::updateTickerPriceFromProvider);
    }

    @CachePut("ticker")
    public TickerDto createTicker(String name, TickerType type) {
        if (type == TickerType.STOCK) {
            TiingoQuoteResponse response = getProviderResponse(name);
            TickerEntity ticker = tickerMapper.toNewEntity(name, type, response);
            TickerEntity savedTicker = tickerRepository.save(ticker);
            return tickerMapper.toDto(savedTicker);
        }

        throw new NotSupportedException();
    }

    private void updateTickerPriceFromProvider(TickerEntity tickerEntity) {
        if (tickerEntity.getType() == TickerType.STOCK) {
            TiingoQuoteResponse response = getProviderResponse(tickerEntity.getTicker());
            TickerEntity updatedEntity = tickerMapper.toEntity(response, tickerEntity);
            tickerRepository.save(updatedEntity);
        }
    }

    private TiingoQuoteResponse getProviderResponse(String ticker) {
        return exchangeQuoteProvider.getLatestPrice(ticker).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No quote found for " + ticker));
    }
}
