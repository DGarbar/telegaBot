package org.dharbar.telegabot.view.view.position;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.dharbar.telegabot.view.model.PositionViewModel;

public class PositionButtonTools extends HorizontalLayout {

    private final Button addBuyOrderButton = new Button();
    private final Button sellAllButton = new Button();
    private final Button editButton = new Button();
    private final Button showOrdersButton = new Button();

    public PositionButtonTools() {
        add(addBuyOrderButton, sellAllButton, editButton, showOrdersButton);
    }

    public void setupAddOrderButton(ComponentEventListener<ClickEvent<Button>> listener) {
        addBuyOrderButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addBuyOrderButton.setTooltipText("Buy");
        addBuyOrderButton.addClickListener(listener);
        addBuyOrderButton.setIcon(VaadinIcon.DIAMOND.create());
    }

    public void setupSellAllButton(PositionViewModel position, ComponentEventListener<ClickEvent<Button>> listener) {
        sellAllButton.setEnabled(!position.getIsClosed());
        sellAllButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        sellAllButton.setTooltipText("Sell");
        sellAllButton.addClickListener(listener);
        sellAllButton.setIcon(VaadinIcon.CASH.create());
    }

    public void setupEditButton(PositionViewModel position, ComponentEventListener<ClickEvent<Button>> listener) {
        if (!position.getAlarms().isEmpty()) {
            editButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            editButton.setIcon(VaadinIcon.WARNING.create());
            editButton.setTooltipText("Alarms");
        } else {
            editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            editButton.setIcon(VaadinIcon.EDIT.create());
            editButton.setTooltipText("Edit");
        }

        editButton.addClickListener(listener);
    }

    public Button setupShowOrdersButton(PositionViewModel position, ComponentEventListener<ClickEvent<Button>> listener) {
        showOrdersButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        showOrdersButton.setTooltipText("Show orders");
        showOrdersButton.addClickListener(listener);
        showOrdersButton.setIcon(VaadinIcon.CLIPBOARD.create());

        if (position.getOrders().isEmpty()) {
            showOrdersButton.setEnabled(false);
        }
        return editButton;
    }
}
