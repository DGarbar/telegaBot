package org.dharbar.telegabot.view.view.stockprice;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.Query;
import lombok.extern.slf4j.Slf4j;
import org.dharbar.telegabot.controller.StockPriceController;
import org.dharbar.telegabot.service.stockprice.dto.StockPriceDto;

import java.util.List;

@Slf4j
public class StockPriceDataProvider extends ListDataProvider<StockPriceDto> {

    private final StockPriceController stockPriceController;

    public StockPriceDataProvider(StockPriceController stockPriceController) {
        super(stockPriceController.getStockPrices());
        this.stockPriceController = stockPriceController;
    }

    // @Override
    // protected Stream<StockPriceDto> fetchFromBackEnd(Query query) {
    //     log.info("Get tickers");
    //     return getTickersResponse(query).stream()
    //             .map(this::toModel);
    // }
    //
    // // TODO fix double request
    // @Override
    // protected int sizeInBackEnd(Query<StockPriceDto, String> query) {
    //     log.info("Get tickers size");
    //     return getTickersResponse(query).size();
    // }

    private List<StockPriceDto> getTickersResponse(Query query) {
        return stockPriceController.getStockPrices();
    }

    public void saveNewTicker(String ticker) {
        try {
            StockPriceDto stockPrice = stockPriceController.createStockPrice(ticker);
            // TODO REMOVE
            refreshItem(stockPrice);
        } catch (Exception e) {
            Notification notification = new Notification("Failed to add new ticker " + ticker, 5000, Notification.Position.TOP_END);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return;
        }

        refreshAll();
    }

    public StockPriceDto getByTicker(String ticker) {
        return getItems().stream()
                .filter(stockPriceDto -> stockPriceDto.getTicker().equals(ticker))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getId(StockPriceDto item) {
        return item.getTicker();
    }

    private StockPriceDto toModel(StockPriceDto tickerResponse) {
        return tickerResponse;
    }
}
