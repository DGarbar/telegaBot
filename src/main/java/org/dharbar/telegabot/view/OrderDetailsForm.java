package org.dharbar.telegabot.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import org.dharbar.telegabot.service.trademanagment.dto.OrderDto;
import org.dharbar.telegabot.view.events.OrderFormEvent;

import java.math.BigDecimal;
import java.util.stream.Stream;

public class OrderDetailsForm extends FormLayout {

    // TODO tiker to combobox
    private final TextField tickerField = new TextField("Ticker");
    private final TextField quantityField = new TextField("Quantity");
    private final TextField rateField = new TextField("Rate");
    private final DatePicker dateAtField = new DatePicker("Date at");
    private final TextField totalUsdField = new TextField("Total USD");
    private final TextField commissionUsdField = new TextField("Commission");
    private final TextField commentField = new TextField("Comment");
    private final Binder<OrderDto> orderDtoBinder;

    private final Button saveButton;

    // TODO for sell auto calculation of fields

    public OrderDetailsForm() {
        addClassName("order-details-form");

        saveButton = new Button("Save");
        Component buttonLayout = buttonLayout(saveButton);

        Stream.of(tickerField, quantityField, rateField, dateAtField, totalUsdField, commissionUsdField, buttonLayout)
                .forEach(this::add);

        // TODO Validation
        orderDtoBinder = new Binder<>(OrderDto.class);
        // TODO into combobox
        orderDtoBinder.bind(tickerField, OrderDto::getTicker, OrderDto::setTicker);
        orderDtoBinder.forField(quantityField)
                .withConverter(BigDecimal::new, BigDecimal::toString)
                .withNullRepresentation(BigDecimal.ZERO)
                .bind(OrderDto::getQuantity, OrderDto::setQuantity);
        orderDtoBinder.forField(rateField)
                .withConverter(BigDecimal::new, BigDecimal::toString)
                .withNullRepresentation(BigDecimal.ZERO)
                .bind(OrderDto::getRate, OrderDto::setRate);
        orderDtoBinder.bind(dateAtField, OrderDto::getDateAt, OrderDto::setDateAt);
        orderDtoBinder.forField(totalUsdField)
                .withConverter(BigDecimal::new, BigDecimal::toString)
                .withNullRepresentation(BigDecimal.ZERO)
                .bind(OrderDto::getTotalUsd, OrderDto::setTotalUsd);
        orderDtoBinder.forField(commissionUsdField)
                .withConverter(BigDecimal::new, BigDecimal::toString)
                .withNullRepresentation(BigDecimal.ZERO)
                .bind(OrderDto::getCommissionUsd, OrderDto::setCommissionUsd);
        orderDtoBinder.bind(commentField, OrderDto::getComment, OrderDto::setComment);

        orderDtoBinder.addStatusChangeListener(event -> saveButton.setEnabled(orderDtoBinder.isValid()));
    }

    private Component buttonLayout(Button saveButton) {
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickShortcut(Key.ENTER);
        saveButton.addClickListener(e -> {
            if (orderDtoBinder.isValid()) {
                fireEvent(new OrderFormEvent.SaveOrderEvent(this, orderDtoBinder.getBean()));
                return;
            }

            // TODO remove
            System.out.println("ERROR");
        });

        Button closeButton = new Button("Close");
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        closeButton.addClickShortcut(Key.ESCAPE);

        closeButton.addClickListener(e -> fireEvent(new OrderFormEvent.CloseEvent(this, orderDtoBinder.getBean())));

        return new HorizontalLayout(saveButton, closeButton);
    }

    public void setOrder(OrderDto orderDto) {
        orderDtoBinder.setBean(orderDto);
    }

    @Override
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return super.addListener(eventType, listener);
    }
}
