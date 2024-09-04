package org.dharbar.telegabot.view.view.order;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.dharbar.telegabot.view.model.OrderViewModel;
import org.dharbar.telegabot.view.view.position.PositionDataProvider;

import java.util.Set;

public class OrderView extends HorizontalLayout {

    private final Grid<OrderViewModel> grid;

    private Button newOrderButton;

    public OrderView(PositionDataProvider positionDataProvider) {
        // addClassName("order-view");
        setSizeFull();

        grid = new OrderGrid(positionDataProvider);

        HorizontalLayout gridToolbar = setupGridToolbar();

        VerticalLayout barAndGridLayout = new VerticalLayout();
        // barAndGridLayout.add(gridToolbar);
        barAndGridLayout.add(grid);
        // barAndGridLayout.setFlexGrow(0, gridToolbar);
        barAndGridLayout.setFlexGrow(1, grid);

        add(barAndGridLayout);
    }

    private HorizontalLayout setupGridToolbar() {
        newOrderButton = new Button("New order", VaadinIcon.PLUS_CIRCLE.create());
        newOrderButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        // newOrderButton.addClickListener(click -> positionForm.showNewPosition(getSelectedPortfolioId()));

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(newOrderButton);
        return topLayout;
    }

    public void setOrders(Set<OrderViewModel> orders) {
        grid.setItems(orders);
    }
}
