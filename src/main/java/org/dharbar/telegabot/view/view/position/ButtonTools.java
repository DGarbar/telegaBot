package org.dharbar.telegabot.view.view.position;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class ButtonTools extends HorizontalLayout {

    private final Button addBuyOrderButton = new Button();
    private final Button sellAllButton = new Button();
    private final Button editButton = new Button();
    private final Button showOrdersButton = new Button();

    public ButtonTools() {
        add(addBuyOrderButton, sellAllButton, editButton, showOrdersButton);
    }

    public void setupAddOrderButton(ComponentEventListener<ClickEvent<Button>> listener)  {
        addBuyOrderButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addBuyOrderButton.addClickListener(listener);
        addBuyOrderButton.setIcon(VaadinIcon.DOLLAR.create());
    }

    public void setupSellAllButton(ComponentEventListener<ClickEvent<Button>> listener)  {
        sellAllButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        sellAllButton.addClickListener(listener);
        sellAllButton.setIcon(VaadinIcon.CASH.create());
    }

    public void setupEditButton(ComponentEventListener<ClickEvent<Button>> listener)  {
        editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        editButton.addClickListener(listener);
        editButton.setIcon(VaadinIcon.EDIT.create());
    }

    public Button setupShowOrdersButton(ComponentEventListener<ClickEvent<Button>> listener) {
        showOrdersButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        showOrdersButton.addClickListener(listener);
        showOrdersButton.setIcon(VaadinIcon.CLIPBOARD.create());
        return editButton;
    }
}