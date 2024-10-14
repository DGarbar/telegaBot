package org.dharbar.telegabot.view.view.ticker;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.dharbar.telegabot.repository.entity.TickerType;
import org.dharbar.telegabot.service.ticker.dto.TickerDto;

import java.util.List;
import java.util.function.Consumer;

public class TickerCreationDialog extends Dialog {

    private final TextField nameField = new TextField("Name");
    private final ComboBox<TickerType> typeComboBox = new ComboBox<>("Type", List.of(TickerType.STOCK, TickerType.CRYPTO));
    private Consumer<TickerDto> afterSaveConsumer;

    public TickerCreationDialog(TickerDataProvider tickerDataProvider) {
        VerticalLayout addPortfolioLayout = createAddPortfolioLayout();
        setHeaderTitle("Add Ticker");
        add(addPortfolioLayout);

        Button saveButton = new Button("Add", e -> {
            TickerDto tickerDto = tickerDataProvider.saveNewTicker(nameField.getValue(), typeComboBox.getValue());
            if (afterSaveConsumer != null) {
                afterSaveConsumer.accept(tickerDto);
            }

            initValues();
            close();
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e -> close());
        getFooter().add(cancelButton);
        getFooter().add(saveButton);
    }

    private void initValues() {
        afterSaveConsumer = null;
        nameField.clear();
        typeComboBox.setValue(TickerType.STOCK);
    }

    private VerticalLayout createAddPortfolioLayout() {
        typeComboBox.setValue(TickerType.STOCK);

        VerticalLayout fieldLayout = new VerticalLayout(nameField, typeComboBox);
        fieldLayout.setSpacing(false);
        fieldLayout.setPadding(false);
        fieldLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        fieldLayout.getStyle().set("width", "300px").set("max-width", "100%");
        return fieldLayout;
    }

    public void open(Consumer<TickerDto> afterSaveConsumer) {
        this.afterSaveConsumer = afterSaveConsumer;
        super.open();
    }
}
