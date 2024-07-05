package org.dharbar.telegabot.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.dharbar.telegabot.repository.entity.OrderType;
import org.dharbar.telegabot.service.stockprice.StockPriceService;
import org.dharbar.telegabot.service.stockprice.dto.StockPriceDto;
import org.dharbar.telegabot.service.trademanagment.TradeAnalyticFacade;
import org.dharbar.telegabot.service.trademanagment.dto.OrderDto;
import org.dharbar.telegabot.service.trademanagment.dto.TradeAnalyticDto;
import org.dharbar.telegabot.view.events.OrderFormEvent;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Route("trades")
@CssImport("./styles/shared-styles.css")
public class TradeView extends VerticalLayout {

    private final Grid<TradeAnalyticDto> grid;
    private final OrderDetailsForm tradeNewForm;
    private final Checkbox isOnlyOpenCheckbox = new Checkbox("Open only", true);

    private final TradeAnalyticFacade tradeAnalyticFacade;

    private final Map<String, BigDecimal> tickerToPrice;

    public TradeView(TradeAnalyticFacade tradeAnalyticFacade, StockPriceService stockPriceService) {
        addClassName("trade-view");
        this.tradeAnalyticFacade = tradeAnalyticFacade;

        tickerToPrice = stockPriceService.findAll().stream()
                .collect(Collectors.toMap(StockPriceDto::getTicker, StockPriceDto::getPrice));
        Set<String> tickers = tickerToPrice.keySet();

        tradeNewForm = new OrderDetailsForm(tickers);
        tradeNewForm.addListener(OrderFormEvent.SaveOrderEvent.class, this::saveOrder);
        tradeNewForm.addListener(OrderFormEvent.CloseEvent.class, e -> closeEditor());
        tradeNewForm.addListener(OrderFormEvent.SaveTickerEvent.class, this::addNewTicker);

        grid = setupGrid();

        Div tradeDetailsDiv = new Div(grid, tradeNewForm);
        tradeDetailsDiv.setSizeFull();
        tradeDetailsDiv.addClassName("trade-details-div");

        add(setupToolbar(), tradeDetailsDiv);
        listTrades();
        closeEditor();
    }

    private Grid<TradeAnalyticDto> setupGrid() {
        Grid<TradeAnalyticDto> grid = new Grid<>(TradeAnalyticDto.class, false);
        grid.addClassName("trade-grid");
        grid.addColumn(TradeAnalyticDto::getTicker).setHeader("Ticker").setKey("ticker").setSortable(true);
        grid.addColumn(TradeAnalyticDto::getDateAt).setHeader("Date").setKey("dateAt").setSortable(true);
        grid.addColumn(TradeAnalyticDto::getBuyTotalUsd).setHeader("Buy $");
        grid.addColumn(TradeAnalyticDto::getBuyRate).setHeader("Buy Rate");

        grid.addColumn(TradeAnalyticDto::getViewSellRate).setHeader("Sell Rate");
        grid.addColumn(TradeAnalyticDto::getViewNetProfitUsd).setHeader("Profit $");
        grid.addColumn(TradeAnalyticDto::getViewProfitPercentage).setHeader("Profit %");

        grid.addColumn(TradeAnalyticDto::getComment).setHeader("Comment");
        grid.getColumns().forEach(column -> column.setAutoWidth(true));

        grid.addColumn(new ComponentRenderer<>(
                        Button::new,
                        (button, trade) -> {
                            button.addThemeVariants(
                                    ButtonVariant.LUMO_ICON,
                                    ButtonVariant.LUMO_ERROR,
                                    ButtonVariant.LUMO_TERTIARY);
                            button.addClickListener(e -> this.openSellOrder(trade));
                            button.setIcon(new Icon(VaadinIcon.CLOSE_CIRCLE));
                            button.setEnabled(!trade.getIsClosed());
                        }))
                .setHeader("Sell");

        // grid.setItemDetailsRenderer(new ComponentRenderer<>(TradeDetailForm::new, TradeDetailForm::setTrade));
        return grid;
    }

    private Component setupToolbar() {
        Button addTradeButton = new Button("Buy new");
        addTradeButton.addClickListener(e -> openBuyOrder());

        isOnlyOpenCheckbox.setValue(true);
        isOnlyOpenCheckbox.addValueChangeListener(e -> listTrades());

        HorizontalLayout toolbar = new HorizontalLayout(isOnlyOpenCheckbox, addTradeButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void openBuyOrder() {
        grid.asSingleSelect().clear();

        OrderDto order = OrderDto.builder()
                .id(UUID.randomUUID())
                .type(OrderType.BUY)
                .dateAt(LocalDate.now())
                .build();

        tradeNewForm.setOrder(order);
        tradeNewForm.setVisible(true);
        addClassName("editing");
    }

    private void saveOrder(OrderFormEvent.SaveOrderEvent newBuyEvent) {
        OrderDto order = newBuyEvent.getOrderDto();
        if (OrderType.BUY == order.getType()) {
            tradeAnalyticFacade.saveNewTrade(order);

        } else {
            TradeAnalyticDto trade = grid.asSingleSelect().getValue();
            tradeAnalyticFacade.addTradeNewOrder(trade.getId(), order);
        }

        listTrades();
        closeEditor();
    }

    private void openSellOrder(TradeAnalyticDto tradeDto) {
        // TODO maybe link through trade
        grid.select(tradeDto);

        OrderDto order = OrderDto.builder()
                .id(UUID.randomUUID())
                .type(OrderType.SELL)
                .ticker(tradeDto.getTicker())
                .rate(tradeDto.getCurrentRate())
                .quantity(tradeDto.getBuyQuantity())
                .commissionUsd(BigDecimal.ZERO)
                .dateAt(LocalDate.now())
                .build();

        tradeNewForm.setOrder(order);
        tradeNewForm.setVisible(true);
        addClassName("editing");
    }

    private void closeEditor() {
        tradeNewForm.setVisible(false);
        // tradeNewForm.setOrder(null);
        removeClassName("editing");
    }

    private void listTrades() {
        if (isOnlyOpenCheckbox.getValue()) {
            // TODO (later) refresh items instead of recreate
            grid.setItems(query -> tradeAnalyticFacade.getOpenTrades(VaadinSpringDataHelpers.toSpringPageRequest(query)).stream());
        } else {
            grid.setItems(query -> tradeAnalyticFacade.getTrades(VaadinSpringDataHelpers.toSpringPageRequest(query)).stream());
        }
    }

    private void addNewTicker(OrderFormEvent.SaveTickerEvent e) {
        String ticker = e.getTicker();
        try {
            StockPriceDto stockPrice = tradeAnalyticFacade.createStockPrice(ticker);
            tickerToPrice.put(ticker, stockPrice.getPrice());
        } catch (Exception ex) {
            Notification notification = new Notification("Failed to add new ticker " + ticker, 5000, Notification.Position.TOP_END);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return;
        }

        tradeNewForm.setTickerValues(tickerToPrice.keySet(), ticker);
    }
}
