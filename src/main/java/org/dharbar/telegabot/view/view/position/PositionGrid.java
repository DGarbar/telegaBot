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
import org.dharbar.telegabot.view.view.position.form.PositionForm;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

public class PositionGrid extends Grid<PositionViewModel> {

    public PositionGrid(PositionForm positionForm,
                        PositionDataProvider positionDataProvider) {
        addClassName("position-grid");
        // setSizeFull();

        addColumn(PositionViewModel::getTicker).setHeader("Ticker").setKey("ticker").setFlexGrow(0)
                .setSortable(true)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setClassNameGenerator(positionAnalyticDto -> StyleUtils.toTickerStyle(positionAnalyticDto.getTicker()));

        addColumn(PositionViewModel::getName).setHeader("Name").setKey("name").setFlexGrow(2);

        Grid.Column<PositionViewModel> openAtColumn = addColumn(PositionViewModel::getOpenAt).setHeader("Date").setKey("openAt").setSortable(true)
                .setFlexGrow(2);
        addColumn(PositionViewModel::getInvestedAmount).setHeader("Invested").setTextAlign(ColumnTextAlign.CENTER).setFlexGrow(3);
        addColumn(PositionViewModel::getBuyAveragePrice).setHeader("Buy Rate").setTextAlign(ColumnTextAlign.CENTER).setFlexGrow(3);

        addColumn(PositionViewModel::getSellAveragePrice).setHeader("Sell Rate").setKey("sellRate").setTextAlign(ColumnTextAlign.CENTER).setFlexGrow(3);
        addColumn(pos -> toAmountUsdAndPercentage(pos.getNetProfitAmount(),
                pos.getProfitPercentage())).setHeader("Profit").setKey("profit").setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(3)
                .setClassNameGenerator(dto -> StyleUtils.toProfitStyle(dto.getProfitPercentage()));

        addColumn(PositionViewModel::getCurrentRatePrice).setHeader("Current Rate").setTextAlign(ColumnTextAlign.CENTER).setFlexGrow(3);
        addColumn(pos -> toAmountUsdAndPercentage(pos.getCurrentNetProfitAmount(),
                pos.getCurrentProfitPercentage())).setHeader("Unreal. Profit").setTextAlign(ColumnTextAlign.CENTER).setFlexGrow(3)
                .setClassNameGenerator(dto -> StyleUtils.toProfitStyle(dto.getCurrentProfitPercentage()));
        // addColumn(PositionViewModel::dealDurationDays).setHeader("Deal days").setTextAlign(ColumnTextAlign.CENTER).setFlexGrow(3);

        // addColumn(PositionViewModel::getComment).setHeader("Comment").setFlexGrow(5);
        // getColumns().forEach(column -> column.setAutoWidth(true));

        OrderDialog orderDialog = new OrderDialog();
        addColumn(getToolButtonRenderer(positionForm, positionDataProvider, orderDialog))
                .setAutoWidth(true)
                .setHeader("Tools").setFlexGrow(7);

        sort(List.of(new GridSortOrder<>(openAtColumn, SortDirection.DESCENDING)));

        setDetailsVisibleOnClick(false);
        setItemDetailsRenderer(new ComponentRenderer<>(() -> new OrderView(positionDataProvider), (orderView, positionViewModel) -> {
            orderView.setOrders(positionViewModel.getOrders());
        }));
    }

    private static String toAmountUsdAndPercentage(BigDecimal amount, BigDecimal percentage) {
        return percentage.setScale(1, RoundingMode.HALF_UP) + "% (" + amount.setScale(1, RoundingMode.HALF_UP) + "$)";
    }

    private static BigDecimal toAmount(BigDecimal amount) {
        return amount.stripTrailingZeros();
    }

    private ComponentRenderer<PositionButtonTools, PositionViewModel> getToolButtonRenderer(PositionForm positionForm,
                                                                                            PositionDataProvider positionDataProvider,
                                                                                            OrderDialog orderDialog) {
        return new ComponentRenderer<>(
                PositionButtonTools::new,
                (tools, position) -> {
                    tools.setupAddOrderButton(e -> {
                        UUID positionId = position.getId();
                        orderDialog.showNewOrder(
                                position.getTicker(),
                                position.getCurrentRatePrice(),
                                positionId,
                                order -> positionDataProvider.addOrderToPosition(positionId, order));
                    });

                    tools.setupSellAllButton(position, e -> {
                        BigDecimal quantity = position.getBuyQuantity().subtract(position.getSellQuantity());
                        UUID positionId = position.getId();
                        orderDialog.showSellAllOrder(
                                position.getTicker(),
                                position.getCurrentRatePrice(),
                                quantity,
                                positionId,
                                order -> positionDataProvider.addOrderToPosition(positionId, order));
                    });
                    tools.setupEditButton(position, e -> positionForm.showPosition(position));
                    tools.setupShowOrdersButton(position, e -> setDetailsVisible(position, !isDetailsVisible(position)));
                });
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


