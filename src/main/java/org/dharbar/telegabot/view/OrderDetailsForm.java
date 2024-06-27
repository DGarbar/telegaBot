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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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

    public static final String DECIMAL_PATTERN = "\\d*\\.?\\d*";

    private final ComboBox<String> tickerComboBox = new ComboBox<>("Ticker");
    private final TextField quantityField = new TextField("Quantity");
    private final TextField rateField = new TextField("Rate");
    private final DatePicker dateAtField = new DatePicker("Date at");
    private final TextField totalUsdField = new TextField("Total USD");
    private final TextField commissionUsdField = new TextField("Commission");
    private final TextField commentField = new TextField("Comment");
    private final Binder<OrderDto> orderDtoBinder;

    private final Button saveButton;

    // TODO for sell auto calculation of fields

    public OrderDetailsForm(Set<String> tickers) {
        addClassName("order-details-form");

        saveButton = new Button("Save");
        Component buttonLayout = buttonLayout(saveButton);

        setupTickerComboBox(tickers);

        quantityField.setPattern(DECIMAL_PATTERN);
        rateField.setPattern(DECIMAL_PATTERN);
        totalUsdField.setPattern(DECIMAL_PATTERN);
        commissionUsdField.setPattern(DECIMAL_PATTERN);

        Stream.of(tickerComboBox, quantityField, rateField, dateAtField, totalUsdField, commissionUsdField, buttonLayout)
                .forEach(this::add);

        orderDtoBinder = setupBinder();
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

    private Binder<OrderDto> setupBinder() {
        final Binder<OrderDto> orderDtoBinder;
        // TODO Validation
        orderDtoBinder = new Binder<>(OrderDto.class);
        orderDtoBinder.bind(tickerComboBox, OrderDto::getTicker, OrderDto::setTicker);
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
        return orderDtoBinder;
    }

    public void setOrder(OrderDto orderDto) {
        orderDtoBinder.setBean(orderDto);
    }

    @Override
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return super.addListener(eventType, listener);
    }
}
