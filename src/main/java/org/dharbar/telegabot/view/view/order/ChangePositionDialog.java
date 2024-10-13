package org.dharbar.telegabot.view.view.order;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.extern.slf4j.Slf4j;
import org.dharbar.telegabot.view.model.OrderViewModel;
import org.dharbar.telegabot.view.model.PositionViewModel;
import org.dharbar.telegabot.view.view.position.PositionDataProvider;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class ChangePositionDialog extends Dialog {

    private final VerticalLayout content;

    private final ComboBox<PositionViewModel> positionComboBox = new ComboBox<>("Position");

    private final Button saveButton = new Button("Save order");
    private final Button closeButton = new Button("Close");
    private final PositionDataProvider positionDataProvider;

    private OrderViewModel currentOrder;

    public ChangePositionDialog(PositionDataProvider positionDataProvider) {
        this.positionDataProvider = positionDataProvider;

        setHeaderTitle("Change position");

        content = new VerticalLayout();
        content.setSpacing(false);
        content.setPadding(false);
        content.setAlignItems(FlexComponent.Alignment.STRETCH);
        content.getStyle().set("width", "300px").set("max-width", "100%");
        add(content);

        setupTickerComboBox();
        Component buttonLayout = setupFunctionalButtonLayout();

        Stream.of(positionComboBox, buttonLayout)
                .forEach(content::add);
    }

    private void setupTickerComboBox() {
        positionComboBox.setRequired(true);
        positionComboBox.setAllowCustomValue(false);
        positionComboBox.setItemLabelGenerator(PositionViewModel::getName);
    }

    private Component setupFunctionalButtonLayout() {
        saveButton.addClickListener(e -> processOrderSave());

        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        closeButton.addClickShortcut(Key.ESCAPE);
        closeButton.addClickListener(e -> close());

        return new HorizontalLayout(saveButton, closeButton);
    }

    private void processOrderSave() {
        PositionViewModel chosenPosition = positionComboBox.getValue();

        if (!currentOrder.getPositionId().equals(chosenPosition.getId())) {
            positionDataProvider.updateOrderNewPosition(chosenPosition.getId(), currentOrder);
        }
        close();
    }

    public void showChangePosition(OrderViewModel order) {
        this.currentOrder = order;

        List<PositionViewModel> positions = positionDataProvider.positionForOrderWithTicker(order.getTicker());
        PositionViewModel orderPosition = positions.stream().filter(p -> p.getId().equals(order.getPositionId())).findFirst().orElse(null);
        positions.remove(orderPosition);

        positionComboBox.setItems(positions);

        open();
    }
}
