package org.dharbar.telegabot.service.ticker;

import jakarta.ws.rs.NotSupportedException;
import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.client.ttingo.TiingoClient;
import org.dharbar.telegabot.client.ttingo.dto.TiingoQuoteResponse;
import org.dharbar.telegabot.repository.TickerRepository;
import org.dharbar.telegabot.repository.entity.TickerEntity;
import org.dharbar.telegabot.repository.entity.TickerType;
import org.dharbar.telegabot.service.pricetrigger.PriceTriggerService;
import org.dharbar.telegabot.service.ticker.dto.TickerDto;
import org.dharbar.telegabot.service.ticker.mapper.TickerMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TickerService {

    private final PriceTriggerService priceTriggerService;

    private final TiingoClient stockExchangeQuoteProvider;

    private final TickerRepository tickerRepository;

    private final TickerMapper tickerMapper;

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
        if (type == TickerType.STOCK) {
            TiingoQuoteResponse response = getProviderResponse(name);
            TickerEntity ticker = tickerMapper.toNewEntity(name, type, response);
            TickerEntity savedTicker = tickerRepository.save(ticker);
            return tickerMapper.toDto(savedTicker);
        }

        throw new NotSupportedException();
    }

    // TODO maybe ignore that has same EOD
    @Transactional
    public void updateEndOfDayTickerPricesFromProvider() {
        tickerRepository.findAll()
                .forEach(this::updateTickerPriceFromProvider);
    }

    private void updateTickerPriceFromProvider(TickerEntity tickerEntity) {
        if (tickerEntity.getType() == TickerType.STOCK) {
            String tickerName = tickerEntity.getTicker();
            TiingoQuoteResponse response = getProviderResponse(tickerName);
            TickerEntity updatedEntity = tickerMapper.toEntity(response, tickerEntity);
            tickerRepository.save(updatedEntity);

            priceTriggerService.checkEndOfDay(tickerName, response.getLow(), response.getHigh(), updatedEntity.getPriceUpdatedAt());
        }
    }

    private TiingoQuoteResponse getProviderResponse(String ticker) {
        // EOD price only
        return stockExchangeQuoteProvider.getLatestPrice(ticker).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No quote found for " + ticker));
    }
}
