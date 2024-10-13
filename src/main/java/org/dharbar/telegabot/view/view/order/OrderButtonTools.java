package org.dharbar.telegabot.view.view.order;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class OrderButtonTools extends HorizontalLayout {

    private final Button editButton = new Button();
    private final Button changePositionButton = new Button();

    public OrderButtonTools() {
        add(editButton, changePositionButton);
    }

    public void setupEditButton(ComponentEventListener<ClickEvent<Button>> listener) {
        editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        editButton.setIcon(VaadinIcon.EDIT.create());
        editButton.setTooltipText("Edit");

        editButton.addClickListener(listener);
    }

    public Button setupChangePositionButton(ComponentEventListener<ClickEvent<Button>> listener) {
        changePositionButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        changePositionButton.setTooltipText("Change position");
        changePositionButton.addClickListener(listener);
        changePositionButton.setIcon(VaadinIcon.ARROW_CIRCLE_RIGHT.create());
        return editButton;
    }
}
