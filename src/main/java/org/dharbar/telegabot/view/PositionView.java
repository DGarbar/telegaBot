package org.dharbar.telegabot.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.dharbar.telegabot.controller.PositionController;
import org.dharbar.telegabot.controller.StockPriceController;
import org.dharbar.telegabot.controller.request.CreateOrderRequest;
import org.dharbar.telegabot.controller.request.CreatePositionRequest;
import org.dharbar.telegabot.repository.entity.OrderType;
import org.dharbar.telegabot.service.stockprice.dto.StockPriceDto;
import org.dharbar.telegabot.view.events.OrderFormEvent;
import org.dharbar.telegabot.view.mapper.PositionViewMapper;
import org.dharbar.telegabot.view.model.OrderViewModel;
import org.dharbar.telegabot.view.model.PositionViewModel;
import org.dharbar.telegabot.view.utils.StyleUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Route("positions")
@CssImport(value = "./styles/shared-styles.css")
@CssImport(value = "./styles/grid.css", themeFor = "vaadin-grid")
public class PositionView extends VerticalLayout {

    private final Grid<PositionViewModel> grid;
    private final OrderCreationForm orderDetailsForm;
    private final Checkbox isOnlyOpenCheckbox = new Checkbox("Open only", true);

    private final PositionController positionController;
    private final StockPriceController stockPriceController;

    private final PositionViewMapper positionViewMapper;

    private final Map<String, BigDecimal> tickerToPrice;

    public PositionView(StockPriceController stockPriceController,
                        PositionController positionController,
                        PositionViewMapper positionViewMapper) {
        this.positionViewMapper = positionViewMapper;
        addClassName("position-view");
        setSizeFull();

        this.positionController = positionController;

        tickerToPrice = stockPriceController.getStockPrices().stream()
                .collect(Collectors.toMap(StockPriceDto::getTicker, StockPriceDto::getPrice));
        Set<String> tickers = tickerToPrice.keySet();

        orderDetailsForm = new OrderCreationForm(tickers);
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
        this.stockPriceController = stockPriceController;
    }

    private Grid<PositionViewModel> setupGrid() {
        Grid<PositionViewModel> grid = new Grid<>(PositionViewModel.class, false);
        grid.addClassName("position-grid");
        grid.addColumn(PositionViewModel::getTicker).setHeader("Ticker").setKey("ticker").setSortable(true)
                .setClassNameGenerator(positionAnalyticDto -> StyleUtils.toTickerStyle(positionAnalyticDto.getTicker()));
        Grid.Column<PositionViewModel> openAtColumn = grid.addColumn(PositionViewModel::getOpenAt).setHeader("Date").setKey("openAt").setSortable(
                true);
        grid.addColumn(PositionViewModel::getBuyTotalAmount).setHeader("Buy $");
        grid.addColumn(PositionViewModel::getBuyAveragePrice).setHeader("Buy Rate");

        grid.addColumn(PositionViewModel::getSellAveragePrice).setHeader("Sell Rate").setKey("sellRate");
        grid.addColumn(PositionViewModel::getNetProfitAmount).setHeader("Profit $").setKey("netProfitAmount")
                .setClassNameGenerator(dto -> StyleUtils.toProfitStyle(dto.getProfitPercentage()));
        grid.addColumn(PositionViewModel::getProfitPercentage).setHeader("Profit %").setKey("profitPercentage")
                .setClassNameGenerator(dto -> StyleUtils.toProfitStyle(dto.getProfitPercentage()));

        grid.addColumn(PositionViewModel::getCurrentRatePrice).setHeader("Current Rate");
        grid.addColumn(PositionViewModel::getCurrentNetProfitAmount).setHeader("Current Profit $")
                .setClassNameGenerator(dto -> StyleUtils.toProfitStyle(dto.getCurrentProfitPercentage()));
        grid.addColumn(PositionViewModel::getCurrentProfitPercentage).setHeader("Current Profit %")
                .setClassNameGenerator(dto -> StyleUtils.toProfitStyle(dto.getCurrentProfitPercentage()));
        grid.addColumn(PositionViewModel::dealDurationDays).setHeader("Deal days");

        grid.addColumn(PositionViewModel::getComment).setHeader("Comment");
        grid.getColumns().forEach(column -> column.setAutoWidth(true));

        grid.addColumn(new ComponentRenderer<>(
                        Button::new,
                        (button, position) -> {
                            button.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                            button.addClickListener(e -> this.viewBuyOrder(position));
                            button.setIcon(VaadinIcon.DOLLAR.create());
                            button.setEnabled(!position.getIsClosed());
                        }))
                .setHeader("Add");
        grid.addColumn(new ComponentRenderer<>(
                        Button::new,
                        (button, position) -> {
                            button.addThemeVariants(ButtonVariant.LUMO_ERROR);
                            button.addClickListener(e -> this.viewSellAllOrder(position));
                            button.setIcon(VaadinIcon.CLOSE_CIRCLE.create());
                            button.setEnabled(!position.getIsClosed());
                        }))
                .setHeader("Sell All");

        grid.sort(List.of(new GridSortOrder<>(openAtColumn, SortDirection.DESCENDING)));
        grid.setSizeFull();

        // grid.setItemDetailsRenderer(new ComponentRenderer<>(PositionsDetailForm::new, PositionsDetailForm::setPositions));
        return grid;
    }

    private Component setupToolbar() {
        Button addPositionsButton = new Button("Buy new");
        addPositionsButton.addClickListener(e -> viewNewBuyOrder());

        isOnlyOpenCheckbox.setValue(true);
        isOnlyOpenCheckbox.addValueChangeListener(e -> listPositions());

        HorizontalLayout toolbar = new HorizontalLayout(isOnlyOpenCheckbox, addPositionsButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void viewBuyOrder(PositionViewModel position) {
        grid.asSingleSelect().clear();

        OrderViewModel order = OrderViewModel.builder()
                .positionId(position.getId())
                .type(OrderType.BUY)
                .ticker(position.getTicker())
                .rate(position.getCurrentRatePrice())
                .commissionAmount(BigDecimal.ZERO)
                .dateAt(LocalDate.now())
                .build();

        orderDetailsForm.setOrder(order);
        orderDetailsForm.setVisible(true);
        addClassName("editing");
    }

    private void viewNewBuyOrder() {
        grid.asSingleSelect().clear();

        OrderViewModel order = OrderViewModel.builder()
                .type(OrderType.BUY)
                .commissionAmount(BigDecimal.ZERO)
                .dateAt(LocalDate.now())
                .build();

        orderDetailsForm.setOrder(order);
        orderDetailsForm.setVisible(true);
        addClassName("editing");
    }

    private void viewSellAllOrder(PositionViewModel position) {
        OrderViewModel order = OrderViewModel.builder()
                .positionId(position.getId())
                .type(OrderType.SELL)
                .ticker(position.getTicker())
                .rate(position.getCurrentRatePrice())
                .quantity(position.getBuyQuantity().subtract(position.getSellQuantity()))
                .commissionAmount(BigDecimal.ZERO)
                .dateAt(LocalDate.now())
                .build();

        orderDetailsForm.setOrder(order);
        orderDetailsForm.setVisible(true);
        addClassName("editing");
    }

    private void saveOrder(OrderFormEvent.SaveOrderEvent newBuyEvent) {
        OrderViewModel order = newBuyEvent.getOrder();
        UUID positionId = order.getPositionId();
        boolean isNewPosition = positionId == null;

        if (isNewPosition) {
            CreatePositionRequest createPositionRequest = positionViewMapper.toCreatePositionRequest(order.getTicker(), List.of(order));
            positionController.createPosition(createPositionRequest);

        } else {
            CreateOrderRequest createOrderRequest = positionViewMapper.toCreateOrderRequest(order);
            positionController.addOrderToPosition(positionId, createOrderRequest);
        }

        listPositions();
        closeEditor();
    }

    private void closeEditor() {
        orderDetailsForm.setVisible(false);
        // positionNewForm.setOrder(null);
        removeClassName("editing");
    }

    private void listPositions() {
        Boolean isOnlyOpen = isOnlyOpenCheckbox.getValue();
        if (isOnlyOpen) {
            // TODO (later) refresh items instead of recreate

            grid.getColumnByKey("sellRate").setVisible(false);
            grid.getColumnByKey("netProfitAmount").setVisible(false);
            grid.getColumnByKey("profitPercentage").setVisible(false);
        } else {
            grid.getColumnByKey("sellRate").setVisible(true);
            grid.getColumnByKey("netProfitAmount").setVisible(true);
            grid.getColumnByKey("profitPercentage").setVisible(true);
        }

        grid.setItems(query ->
                positionController.getPositions(isOnlyOpen, VaadinSpringDataHelpers.toSpringPageRequest(query)).stream()
                        .map(positionResponse -> {
                            List<OrderViewModel> orders = positionViewMapper.toModels(positionResponse.getOrders(), positionResponse.getId());
                            return positionViewMapper.toModel(positionResponse, orders);
                        }));
    }

    private void addNewTicker(OrderFormEvent.SaveTickerEvent event) {
        String ticker = event.getTicker();
        try {
            StockPriceDto stockPrice = stockPriceController.createStockPrice(ticker);
            tickerToPrice.put(ticker, stockPrice.getPrice());
        } catch (Exception e) {
            Notification notification = new Notification("Failed to add new ticker " + ticker, 5000, Notification.Position.TOP_END);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return;
        }

        orderDetailsForm.setTickerValues(tickerToPrice.keySet(), ticker);
    }
}
