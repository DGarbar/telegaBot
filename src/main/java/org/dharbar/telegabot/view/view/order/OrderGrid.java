package org.dharbar.telegabot.view.view.order;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.icon.VaadinIcon;
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
        addColumn(OrderViewModel::getRate).setHeader("Rate").setTextAlign(ColumnTextAlign.END).setFlexGrow(3);
        addColumn(OrderViewModel::getQuantity).setHeader("Quantity").setTextAlign(ColumnTextAlign.END).setFlexGrow(3);
        addColumn(OrderViewModel::getTotalUsd).setHeader("Total Buy").setKey("sellRate").setTextAlign(ColumnTextAlign.END).setFlexGrow(3);
        addColumn(OrderViewModel::getType).setHeader("Type").setKey("type").setTextAlign(ColumnTextAlign.END).setFlexGrow(3);
        addColumn(OrderViewModel::getComment).setHeader("Comment").setFlexGrow(10);


        OrderDialog orderDialog = new OrderDialog();
        addColumn(new ComponentRenderer<>(
                Button::new,
                (button, order) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                    button.addClickListener(e -> orderDialog.showEdit(order, o -> positionDataProvider.updateOrderPosition(order.getPositionId(), o)));
                    button.setIcon(VaadinIcon.EDIT.create());
                }))
                .setHeader("Edit").setFlexGrow(5);

        sort(List.of(new GridSortOrder<>(date, SortDirection.DESCENDING)));
    }

    public void refresh(OrderViewModel orderViewModel) {
        getDataCommunicator().refresh(orderViewModel);
    }
}


