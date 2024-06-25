package org.dharbar.telegabot.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import org.dharbar.telegabot.repository.entity.OrderType;
import org.dharbar.telegabot.service.trademanagment.TradeService;
import org.dharbar.telegabot.service.trademanagment.dto.OrderDto;
import org.dharbar.telegabot.service.trademanagment.dto.TradeDto;
import org.dharbar.telegabot.view.events.OrderFormEvent;

import java.time.LocalDate;
import java.util.UUID;

@Route("trades")
@CssImport("./styles/shared-styles.css")
public class TradeView extends VerticalLayout {

    private final Grid<TradeDto> grid;
    private final OrderDetailsForm tradeNewForm;

    private final TradeService tradeService;

    public TradeView(TradeService tradeService) {
        addClassName("trade-view");
        this.tradeService = tradeService;

        tradeNewForm = new OrderDetailsForm();
        tradeNewForm.addListener(OrderFormEvent.SaveOrderEvent.class, this::saveOrder);
        tradeNewForm.addListener(OrderFormEvent.CloseEvent.class, e -> closeEditor());

        grid = setupGrid();

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
        grid.addColumn(TradeDto::getTicker).setHeader("Ticker").setSortable(true);
        grid.addColumn(TradeDto::getBuyDateAt).setHeader("Date").setSortable(true);
        grid.addColumn(TradeDto::getBuyTotalUsd).setHeader("Buy $");
        grid.addColumn(TradeDto::getBuyRate).setHeader("Buy Rate");
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
            TradeDto trade = TradeDto.builder()
                    .byuOrder(order)
                    .build();
            tradeService.saveTrade(trade);

        } else {
            TradeDto trade = grid.asSingleSelect().getValue();
            tradeService.saveSellOrder(trade.getId(), order);
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
        tradeNewForm.setOrder(null);
        tradeNewForm.setVisible(false);
        removeClassName("editing");
    }

    private void listTrades() {
        // TODO add filtering and pagination
        // grid.setItems(VaadinSpringDataHelpers.fromPagingRepository(repo))
        grid.setItems(tradeService.getTrades());
    }

    // todo add filtering
    // by ticker
    // by completed
}
