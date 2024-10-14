package org.dharbar.telegabot.view.view.ticker;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.Query;
import lombok.extern.slf4j.Slf4j;
import org.dharbar.telegabot.controller.TickerController;
import org.dharbar.telegabot.controller.request.CreateTickerRequest;
import org.dharbar.telegabot.repository.entity.TickerType;
import org.dharbar.telegabot.service.ticker.dto.TickerDto;
import org.dharbar.telegabot.view.mapper.TickerViewMapper;

import java.util.Set;

@Slf4j
public class TickerDataProvider extends ListDataProvider<TickerDto> {

    private final TickerController tickerController;
    private final TickerViewMapper tickerViewMapper;

    public TickerDataProvider(TickerController tickerController, TickerViewMapper tickerViewMapper) {
        super(tickerController.getTickers());
        this.tickerController = tickerController;
        this.tickerViewMapper = tickerViewMapper;
    }

    // @Override
    // protected Stream<TickerDto> fetchFromBackEnd(Query query) {
    //     log.info("Get tickers");
    //     return getTickersResponse(query).stream()
    //             .map(this::toModel);
    // }
    // @Override
    // protected int sizeInBackEnd(Query<TickerDto, String> query) {
    //     log.info("Get tickers size");
    //     return getTickersResponse(query).size();
    // }

    private Set<TickerDto> getTickersResponse(Query query) {
        return tickerController.getTickers();
    }

    public TickerDto saveNewTicker(String tickerName, TickerType type) {
        try {
            CreateTickerRequest createTickerRequest = tickerViewMapper.toCreateTickerRequest(tickerName, type);
            TickerDto ticker = tickerController.createTicker(createTickerRequest);

            getItems().add(ticker);
            refreshAll();
            return ticker;
        } catch (Exception e) {
            Notification notification = new Notification("Failed to add new ticker " + tickerName, 5000, Notification.Position.TOP_END);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            throw e;
        }
    }

    public TickerDto getByTicker(String ticker) {
        return getItems().stream()
                .filter(tickerDto -> tickerDto.getTicker().equals(ticker))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getId(TickerDto item) {
        return item.getTicker();
    }

    private TickerDto toModel(TickerDto tickerResponse) {
        return tickerResponse;
    }
}
