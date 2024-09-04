package org.dharbar.telegabot.view.view.position;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.dharbar.telegabot.view.model.PositionViewModel;
import org.dharbar.telegabot.view.utils.StyleUtils;
import org.dharbar.telegabot.view.view.order.OrderDialog;
import org.dharbar.telegabot.view.view.order.OrderView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

public class PositionGrid extends Grid<PositionViewModel> {

    public PositionGrid(PositionForm positionForm,
                        PositionDataProvider positionDataProvider) {
        addClassName("position-grid");
        setSizeFull();

        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setMinimumFractionDigits(2);

        addColumn(PositionViewModel::getTicker).setHeader("Ticker").setKey("ticker").setFlexGrow(5)
                .setSortable(true)
                .setClassNameGenerator(positionAnalyticDto -> StyleUtils.toTickerStyle(positionAnalyticDto.getTicker()));
        Grid.Column<PositionViewModel> openAtColumn = addColumn(PositionViewModel::getOpenAt).setHeader("Date").setKey("openAt").setSortable(true).setFlexGrow(5);
        // TODO
        // product -> decimalFormat.format(product.getPrice()) + " $")
        addColumn(PositionViewModel::getBuyTotalAmount).setHeader("Invested").setTextAlign(ColumnTextAlign.END).setFlexGrow(3);
        addColumn(PositionViewModel::getBuyAveragePrice).setHeader("Buy Rate").setTextAlign(ColumnTextAlign.END).setFlexGrow(3);

        addColumn(PositionViewModel::getSellAveragePrice).setHeader("Sell Rate").setKey("sellRate").setTextAlign(ColumnTextAlign.END).setFlexGrow(3);
        addColumn(PositionViewModel::getNetProfitAmount).setHeader("Profit $").setKey("netProfitAmount").setTextAlign(ColumnTextAlign.END).setFlexGrow(
                        3)
                .setClassNameGenerator(dto -> StyleUtils.toProfitStyle(dto.getProfitPercentage()));
        addColumn(PositionViewModel::getProfitPercentage).setHeader("Profit %").setKey("profitPercentage").setTextAlign(ColumnTextAlign.END).setFlexGrow(
                        3)
                .setClassNameGenerator(dto -> StyleUtils.toProfitStyle(dto.getProfitPercentage()));

        addColumn(PositionViewModel::getCurrentRatePrice).setHeader("Current Rate").setTextAlign(ColumnTextAlign.END).setFlexGrow(3);
        addColumn(PositionViewModel::getCurrentNetProfitAmount).setHeader("Current Profit $").setTextAlign(ColumnTextAlign.END).setFlexGrow(3)
                .setClassNameGenerator(dto -> StyleUtils.toProfitStyle(dto.getCurrentProfitPercentage()));
        addColumn(PositionViewModel::getCurrentProfitPercentage).setHeader("Current Profit %").setTextAlign(ColumnTextAlign.END).setFlexGrow(3)
                .setClassNameGenerator(dto -> StyleUtils.toProfitStyle(dto.getCurrentProfitPercentage()));
        addColumn(PositionViewModel::dealDurationDays).setHeader("Deal days").setTextAlign(ColumnTextAlign.END).setFlexGrow(3);

        addColumn(PositionViewModel::getComment).setHeader("Comment").setFlexGrow(10);
        // getColumns().forEach(column -> column.setAutoWidth(true));

        OrderDialog orderDialog = new OrderDialog();
        addColumn(new ComponentRenderer<>(
                ButtonTools::new,
                (tools, position) -> {
                    tools.setupAddOrderButton(e -> {
                        UUID positionId = position.getId();
                        orderDialog.showNewOrder(
                                position.getTicker(),
                                BigDecimal.ZERO,
                                positionId,
                                order -> positionDataProvider.addOrderToPosition(positionId, order));
                    });

                    tools.setupSellAllButton(e -> {
                        BigDecimal quantity = position.getBuyQuantity().subtract(position.getSellQuantity());
                        UUID positionId = position.getId();
                        orderDialog.showSellAllOrder(
                                position.getTicker(),
                                position.getCurrentRatePrice(),
                                quantity,
                                positionId,
                                order -> positionDataProvider.addOrderToPosition(positionId, order));
                    });
                    tools.setupEditButton(e -> positionForm.showPosition(position));
                    tools.setupShowOrdersButton(e -> setDetailsVisible(position, !isDetailsVisible(position)));
                }))
                .setHeader("Tools").setFlexGrow(5);

        sort(List.of(new GridSortOrder<>(openAtColumn, SortDirection.DESCENDING)));

        setDetailsVisibleOnClick(false);
        setItemDetailsRenderer(new ComponentRenderer<>(() -> new OrderView(positionDataProvider), (orderView, positionViewModel) -> {
            orderView.setOrders(positionViewModel.getOrders());
        }));
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        // fetch browser width
        UI.getCurrent().getInternals().setExtendedClientDetails(null);
        // UI.getCurrent().getPage().retrieveExtendedClientDetails(e -> {
        //     setColumnVisibility(e.getBodyClientWidth());
        // });
    }

    public void refresh(PositionViewModel positionViewModel) {
        getDataCommunicator().refresh(positionViewModel);
    }

}


