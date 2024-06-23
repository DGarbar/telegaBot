package org.dharbar.telegabot.view;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.dharbar.telegabot.service.trademanagment.dto.TradeDto;

import java.util.stream.Stream;

public class TradeDetailForm extends FormLayout {

    // TODO Cell Focus

    private final TextField buyQuantityField = new TextField("Buy quantity");
    // private final TextField phoneField = new TextField("Phone number");
    // private final TextField streetField = new TextField("Street address");
    // private final TextField zipField = new TextField("ZIP code");
    // private final TextField cityField = new TextField("City");
    // private final TextField stateField = new TextField("State");

    public TradeDetailForm() {
        Stream.of(buyQuantityField)
                .forEach(field -> {
                    field.setReadOnly(true);
                    add(field);
                });

        setResponsiveSteps(new ResponsiveStep("0", 3));
        setColspan(buyQuantityField, 3);
        // setColspan(phoneField, 3);
        // setColspan(streetField, 3);
    }

    public void setTrade(TradeDto trade) {
        buyQuantityField.setValue(trade.getBuyQuantity().toString());
    }
}
