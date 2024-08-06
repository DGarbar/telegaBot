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
import org.dharbar.telegabot.service.positionmanagment.PositionsAnalyticFacade;
import org.dharbar.telegabot.service.positionmanagment.dto.OrderDto;
import org.dharbar.telegabot.service.positionmanagment.dto.PositionAnalyticDto;
import org.dharbar.telegabot.service.stockprice.StockPriceService;
import org.dharbar.telegabot.service.stockprice.dto.StockPriceDto;
import org.dharbar.telegabot.view.events.OrderFormEvent;
import org.dharbar.telegabot.view.utils.StyleUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Route("positions")
@CssImport(value = "./styles/shared-styles.css")
@CssImport(value = "./styles/grid.css", themeFor = "vaadin-grid")
public class PositionView extends VerticalLayout  {

    private final Grid<PositionAnalyticDto> grid;
    private final OrderDetailsForm orderDetailsForm;
    private final Checkbox isOnlyOpenCheckbox = new Checkbox("Open only", true);

    private final PositionsAnalyticFacade positionAnalyticFacade;

    private final Map<String, BigDecimal> tickerToPrice;

    public PositionView(PositionsAnalyticFacade positionAnalyticFacade, StockPriceService stockPriceService) {
        addClassName("position-view");
        setSizeFull();

        this.positionAnalyticFacade = positionAnalyticFacade;

        tickerToPrice = stockPriceService.findAll().stream()
                .collect(Collectors.toMap(StockPriceDto::getTicker, StockPriceDto::getPrice));
        Set<String> tickers = tickerToPrice.keySet();

        orderDetailsForm = new OrderDetailsForm(tickers);
        orderDetailsForm.addListener(OrderFormEvent.SaveOrderEvent.class, this::saveOrder);
        orderDetailsForm.addListener(OrderFormEvent.CloseEvent.class, e -> closeEditor());
        orderDetailsForm.addListener(OrderFormEvent.SaveTickerEvent.class, this::addNewTicker);

        grid = setupGrid();

        Div positionDetailsDiv = new Div(grid, orderDetailsForm);
        positionDetailsDiv.setSizeFull();
        positionDetailsDiv.addClassName("position-details-div");

        add(setupToolbar(), positionDetailsDiv);
        listPositions();
        closeEditor();
    }

    private Grid<PositionAnalyticDto> setupGrid() {
        Grid<PositionAnalyticDto> grid = new Grid<>(PositionAnalyticDto.class, false);
        grid.addClassName("position-grid");
        grid.addColumn(PositionAnalyticDto::getTicker).setHeader("Ticker").setKey("ticker").setSortable(true)
                .setClassNameGenerator(positionAnalyticDto -> StyleUtils.toTickerStyle(positionAnalyticDto.getTicker()));
        grid.addColumn(PositionAnalyticDto::getDateAt).setHeader("Date").setKey("dateAt").setSortable(true);
        grid.addColumn(PositionAnalyticDto::getBuyTotalUsd).setHeader("Buy $");
        grid.addColumn(PositionAnalyticDto::getBuyRate).setHeader("Buy Rate");

        grid.addColumn(PositionAnalyticDto::getSellRate).setHeader("Sell Rate").setKey("sellRate");
        grid.addColumn(PositionAnalyticDto::getNetProfitUsd).setHeader("Profit $").setKey("profitDollar")
                .setClassNameGenerator(dto -> StyleUtils.toPercentageProfitStyle(dto.getProfitPercentage()));
        grid.addColumn(PositionAnalyticDto::getProfitPercentage).setHeader("Profit %").setKey("profitPercentage")
                .setClassNameGenerator(dto -> StyleUtils.toPercentageProfitStyle(dto.getProfitPercentage()));

        grid.addColumn(PositionAnalyticDto::getCurrentRate).setHeader("Current Rate");
        grid.addColumn(PositionAnalyticDto::getCurrentNetProfitUsd).setHeader("Current Profit $")
                .setClassNameGenerator(dto -> StyleUtils.toPercentageProfitStyle(dto.getCurrentProfitPercentage()));
        grid.addColumn(PositionAnalyticDto::getCurrentProfitPercentage).setHeader("Current Profit %")
                .setClassNameGenerator(dto -> StyleUtils.toPercentageProfitStyle(dto.getCurrentProfitPercentage()));
        grid.addColumn(PositionAnalyticDto::dealDurationDays).setHeader("Deal days");

        grid.addColumn(PositionAnalyticDto::getComment).setHeader("Comment");
        grid.getColumns().forEach(column -> column.setAutoWidth(true));

        grid.addColumn(new ComponentRenderer<>(
                        Button::new,
                        (button, position) -> {
                            button.addThemeVariants(
                                    ButtonVariant.LUMO_ICON,
                                    ButtonVariant.LUMO_ERROR,
                                    ButtonVariant.LUMO_TERTIARY);
                            button.addClickListener(e -> this.openSellOrder(position));
                            button.setIcon(new Icon(VaadinIcon.CLOSE_CIRCLE));
                            button.setEnabled(!position.getIsClosed());
                        }))
                .setHeader("Sell");

        grid.setSizeFull();

        // grid.setItemDetailsRenderer(new ComponentRenderer<>(PositionsDetailForm::new, PositionsDetailForm::setPositions));
        return grid;
    }


    private Component setupToolbar() {
        Button addPositionsButton = new Button("Buy new");
        addPositionsButton.addClickListener(e -> openBuyOrder());

        isOnlyOpenCheckbox.setValue(true);
        isOnlyOpenCheckbox.addValueChangeListener(e -> listPositions());

        HorizontalLayout toolbar = new HorizontalLayout(isOnlyOpenCheckbox, addPositionsButton);
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

        orderDetailsForm.setOrder(order);
        orderDetailsForm.setVisible(true);
        addClassName("editing");
    }

    private void saveOrder(OrderFormEvent.SaveOrderEvent newBuyEvent) {
        OrderDto order = newBuyEvent.getOrderDto();
        if (OrderType.BUY == order.getType()) {
            positionAnalyticFacade.saveNewPosition(order);

        } else {
            PositionAnalyticDto position = grid.asSingleSelect().getValue();
            positionAnalyticFacade.addPositionNewOrder(position.getId(), order);
        }

        listPositions();
        closeEditor();
    }

    private void openSellOrder(PositionAnalyticDto positionDto) {
        // TODO maybe link through position
        grid.select(positionDto);

        OrderDto order = OrderDto.builder()
                .id(UUID.randomUUID())
                .type(OrderType.SELL)
                .ticker(positionDto.getTicker())
                .rate(positionDto.getCurrentRate())
                .quantity(positionDto.getBuyQuantity())
                .commissionUsd(BigDecimal.ZERO)
                .dateAt(LocalDate.now())
                .build();

        orderDetailsForm.setOrder(order);
        orderDetailsForm.setVisible(true);
        addClassName("editing");
    }

    private void closeEditor() {
        orderDetailsForm.setVisible(false);
        // positionNewForm.setOrder(null);
        removeClassName("editing");
    }

    private void listPositions() {
        if (isOnlyOpenCheckbox.getValue()) {
            // TODO (later) refresh items instead of recreate

            grid.getColumnByKey("sellRate").setVisible(false);
            grid.getColumnByKey("profitDollar").setVisible(false);
            grid.getColumnByKey("profitPercentage").setVisible(false);

            grid.setItems(query -> positionAnalyticFacade.getOpenPositions(VaadinSpringDataHelpers.toSpringPageRequest(query)).stream());
        } else {

            grid.getColumnByKey("sellRate").setVisible(true);
            grid.getColumnByKey("profitDollar").setVisible(true);
            grid.getColumnByKey("profitPercentage").setVisible(true);

            grid.setItems(query -> positionAnalyticFacade.getPositions(VaadinSpringDataHelpers.toSpringPageRequest(query)).stream());
        }
    }

    private void addNewTicker(OrderFormEvent.SaveTickerEvent e) {
        String ticker = e.getTicker();
        try {
            StockPriceDto stockPrice = positionAnalyticFacade.createStockPrice(ticker);
            tickerToPrice.put(ticker, stockPrice.getPrice());
        } catch (Exception ex) {
            Notification notification = new Notification("Failed to add new ticker " + ticker, 5000, Notification.Position.TOP_END);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return;
        }

        orderDetailsForm.setTickerValues(tickerToPrice.keySet(), ticker);
    }
}
