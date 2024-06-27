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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.dharbar.telegabot.repository.entity.OrderType;
import org.dharbar.telegabot.service.trademanagment.TradeService;
import org.dharbar.telegabot.service.trademanagment.dto.OrderDto;
import org.dharbar.telegabot.service.trademanagment.dto.TradeDto;
import org.dharbar.telegabot.view.events.OrderFormEvent;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Route("trades")
@CssImport("./styles/shared-styles.css")
public class TradeView extends VerticalLayout {

    private final Grid<TradeDto> grid;
    private final OrderDetailsForm tradeNewForm;
    private final Checkbox isOnlyOpenCheckbox = new Checkbox("Open only", true);

    private final TradeService tradeService;

    public TradeView(TradeService tradeService) {
        addClassName("trade-view");
        this.tradeService = tradeService;

        Set<String> tickers = tradeService.getTickers();
        tradeNewForm = new OrderDetailsForm(tickers);
        tradeNewForm.addListener(OrderFormEvent.SaveOrderEvent.class, this::saveOrder);
        tradeNewForm.addListener(OrderFormEvent.CloseEvent.class, e -> closeEditor());

        grid = setupGrid();

        isOnlyOpenCheckbox.setValue(true);
        isOnlyOpenCheckbox.addValueChangeListener(e -> listTrades());

        Div tradeDetailsDiv = new Div(grid, tradeNewForm);
        tradeDetailsDiv.setSizeFull();
        tradeDetailsDiv.addClassName("trade-details-div");

        add(setupToolbar(), tradeDetailsDiv);
        listTrades();
        closeEditor();
    }

    private Grid<TradeDto> setupGrid() {
        Grid<TradeDto> grid = new Grid<>(TradeDto.class, false);
        grid.addClassName("trade-grid");
        grid.addColumn(TradeDto::getTicker).setHeader("Ticker").setKey("ticker").setSortable(true);
        grid.addColumn(TradeDto::getDateAt).setHeader("Date").setKey("dateAt").setSortable(true);
        grid.addColumn(TradeDto::getBuyTotalUsd).setHeader("Buy $");
        grid.addColumn(TradeDto::getBuyRate).setHeader("Buy Rate");
        // grid.addColumn(TradeDto::getSellOrders).setHeader("Sell Rate");
        grid.addColumn(TradeDto::getNetProfitUsd).setHeader("Profit $");
        grid.addColumn(TradeDto::getProfitPercentage).setHeader("Profit %");
        grid.addColumn(TradeDto::getComment).setHeader("Comment");
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

        HorizontalLayout toolbar = new HorizontalLayout(addTradeButton);
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
            tradeService.saveNewTrade(order);

        } else {
            TradeDto trade = grid.asSingleSelect().getValue();
            tradeService.saveTradeSellOrder(trade.getId(), order);
        }

        listTrades();
        closeEditor();
    }

    private void openSellOrder(TradeDto tradeDto) {
        // TODO maybe link through trade
        grid.select(tradeDto);

        OrderDto order = OrderDto.builder()
                .id(UUID.randomUUID())
                .type(OrderType.SELL)
                .ticker(tradeDto.getTicker())
                .rate(tradeDto.getBuyRate())
                .quantity(tradeDto.getBuyQuantity())
                .totalUsd(tradeDto.getBuyTotalUsd())
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
        // TODO add filtering and pagination
        // grid.setItems(VaadinSpringDataHelpers.fromPagingRepository(repo))
        if (isOnlyOpenCheckbox.getValue()) {
            grid.setItems(query -> tradeService.getOpenTrades(VaadinSpringDataHelpers.toSpringPageRequest(query)).stream());
        } else {
            grid.setItems(query -> tradeService.getTrades(VaadinSpringDataHelpers.toSpringPageRequest(query)).stream());
        }
    }

    private static PageRequest toPageRequest(Query<TradeDto, Void> query) {
        List<Sort.Order> sorting = query.getSortOrders().stream()
                .filter(sortOrder -> SortDirection.ASCENDING == sortOrder.getDirection())
                .map(QuerySortOrder::getSorted)
                .map(Sort.Order::asc)
                .toList();

        return PageRequest.of(query.getPage(), query.getPageSize(), Sort.by(sorting));
    }

    // todo add filtering
    // by ticker
    // by completed
}
