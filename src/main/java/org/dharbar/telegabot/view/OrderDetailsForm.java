package org.dharbar.telegabot.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import lombok.extern.slf4j.Slf4j;
import org.dharbar.telegabot.service.trademanagment.dto.OrderDto;
import org.dharbar.telegabot.view.events.OrderFormEvent;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Stream;

@Slf4j
public class OrderDetailsForm extends FormLayout {

    private final ComboBox<String> tickerComboBox = new ComboBox<>("Ticker");
    private final BigDecimalField quantityField = new BigDecimalField("Quantity");
    private final BigDecimalField rateField = new BigDecimalField("Rate");
    private final DatePicker dateAtField = new DatePicker("Date at");
    private final BigDecimalField totalUsdField = new BigDecimalField("Total");
    private final BigDecimalField commissionUsdField = new BigDecimalField("Commission");
    private final TextField commentField = new TextField("Comment");
    private final Binder<OrderDto> orderDtoBinder = new Binder<>(OrderDto.class);

    private final Button saveButton = new Button("Save");

    public OrderDetailsForm(Set<String> tickers) {
        addClassName("order-details-form");

        Component buttonLayout = buttonLayout(saveButton);

        setupTickerComboBox(tickers);
        setupFields();

        Stream.of(tickerComboBox, quantityField, rateField, dateAtField, totalUsdField, commissionUsdField, buttonLayout)
                .forEach(this::add);

        setupBinder();
    }

    private Component buttonLayout(Button saveButton) {
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickShortcut(Key.ENTER);
        saveButton.addClickListener(e -> {
            if (orderDtoBinder.isValid()) {
                fireEvent(new OrderFormEvent.SaveOrderEvent(this, orderDtoBinder.getBean()));
                return;
            }

            log.error("! Validation failed !");
        });

        Button closeButton = new Button("Close");
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        closeButton.addClickShortcut(Key.ESCAPE);

        closeButton.addClickListener(e -> fireEvent(new OrderFormEvent.CloseEvent(this, orderDtoBinder.getBean())));

        return new HorizontalLayout(saveButton, closeButton);
    }

    private void setupTickerComboBox(Set<String> tickers) {
        tickerComboBox.setItems(tickers);
        tickerComboBox.setRequired(true);
        tickerComboBox.setAllowCustomValue(true);
        tickerComboBox.setAllowedCharPattern("[A-Z]");
        tickerComboBox.addCustomValueSetListener(e -> {
            String customValue = e.getDetail();
            tickers.add(customValue);
            tickerComboBox.setItems(tickers);
            tickerComboBox.setValue(customValue);
        });
    }

    private void setupFields() {
        commissionUsdField.setSuffixComponent(VaadinIcon.DOLLAR.create());
        rateField.setSuffixComponent(VaadinIcon.DOLLAR.create());
        rateField.addValueChangeListener(e -> recalculateTotalField());
        quantityField.addValueChangeListener(e -> recalculateTotalField());
        totalUsdField.setSuffixComponent(VaadinIcon.DOLLAR.create());
    }

    private void recalculateTotalField() {
        BigDecimal quantity = quantityField.getValue();
        BigDecimal rate = rateField.getValue();
        if (quantity == null || rate == null) {
            return;
        }
        BigDecimal totalUsd = quantity.multiply(rate).stripTrailingZeros();
        totalUsdField.setValue(totalUsd);
    }

    private void setupBinder() {
        orderDtoBinder.bind(tickerComboBox, OrderDto::getTicker, OrderDto::setTicker);
        orderDtoBinder.forField(quantityField).bind(OrderDto::getQuantity, OrderDto::setQuantity);
        orderDtoBinder.forField(rateField).bind(OrderDto::getRate, OrderDto::setRate);
        orderDtoBinder.bind(dateAtField, OrderDto::getDateAt, OrderDto::setDateAt);
        orderDtoBinder.forField(totalUsdField).bind(OrderDto::getTotalUsd, OrderDto::setTotalUsd);
        orderDtoBinder.forField(commissionUsdField).bind(OrderDto::getCommissionUsd, OrderDto::setCommissionUsd);
        orderDtoBinder.bind(commentField, OrderDto::getComment, OrderDto::setComment);

        orderDtoBinder.addStatusChangeListener(event -> saveButton.setEnabled(orderDtoBinder.isValid()));
    }

    public void setOrder(OrderDto orderDto) {
        orderDtoBinder.setBean(orderDto);
    }

    @Override
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return super.addListener(eventType, listener);
    }
}
