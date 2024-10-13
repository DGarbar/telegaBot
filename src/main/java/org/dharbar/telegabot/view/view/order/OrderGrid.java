package org.dharbar.telegabot.view.view.order;

import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.dharbar.telegabot.view.model.OrderViewModel;
import org.dharbar.telegabot.view.view.position.PositionDataProvider;

import java.text.DecimalFormat;
import java.util.List;

public class OrderGrid extends Grid<OrderViewModel> {

    public OrderGrid(PositionDataProvider positionDataProvider) {
        addClassName("order-grid");
        setAllRowsVisible(true);

        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setMinimumFractionDigits(2);

        Column<OrderViewModel> date = addColumn(OrderViewModel::getDateAt).setHeader("Date").setKey("dateAt").setSortable(true).setFlexGrow(5);
        addColumn(OrderViewModel::getTotalUsd).setHeader("Total Buy").setKey("sellRate").setTextAlign(ColumnTextAlign.CENTER).setFlexGrow(3);
        addColumn(OrderViewModel::getRate).setHeader("Rate").setTextAlign(ColumnTextAlign.CENTER).setFlexGrow(3);
        addColumn(OrderViewModel::getQuantity).setHeader("Quantity").setTextAlign(ColumnTextAlign.CENTER).setFlexGrow(3);
        addColumn(OrderViewModel::getType).setHeader("Type").setKey("type").setTextAlign(ColumnTextAlign.CENTER).setFlexGrow(3);
        addColumn(OrderViewModel::getComment).setHeader("Comment").setFlexGrow(5);


        OrderDialog orderDialog = new OrderDialog();
        ChangePositionDialog changePositionDialog = new ChangePositionDialog(positionDataProvider);
        addColumn(new ComponentRenderer<>(
                OrderButtonTools::new,
                (tools, order) -> {
                    tools.setupEditButton(e -> orderDialog.showEdit(order, o -> positionDataProvider.updateOrderPosition(order.getPositionId(), o)));
                    tools.setupChangePositionButton(e -> changePositionDialog.showChangePosition(order));
                }))
                .setHeader("Edit").setFlexGrow(5);

        sort(List.of(new GridSortOrder<>(date, SortDirection.DESCENDING)));
    }

    public void refresh(OrderViewModel orderViewModel) {
        getDataCommunicator().refresh(orderViewModel);
    }
}


