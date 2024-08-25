package org.dharbar.telegabot.view.positionview;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import lombok.extern.slf4j.Slf4j;
import org.dharbar.telegabot.service.stockprice.dto.StockPriceDto;
import org.dharbar.telegabot.view.events.OrderFormEvent;
import org.dharbar.telegabot.view.model.OrderViewModel;
import org.dharbar.telegabot.view.positionview.ticker.StockPriceDataProvider;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
public class OrderCreationForm extends Div {

    private final VerticalLayout content;

    private final ComboBox<StockPriceDto> tickerComboBox = new ComboBox<>("Ticker");
    private final BigDecimalField quantityField = new BigDecimalField("Quantity");
    private final BigDecimalField rateField = new BigDecimalField("Rate");
    private final DatePicker dateAtField = new DatePicker("Date at");
    private final BigDecimalField totalUsdField = new BigDecimalField("Total");
    private final BigDecimalField commissionAmountField = new BigDecimalField("Commission");
    private final TextField commentField = new TextField("Comment");
    private final Binder<OrderViewModel> orderDtoBinder = new Binder<>(OrderViewModel.class);

    private final Button saveButton = new Button("Save");

    public OrderCreationForm(StockPriceDataProvider stockPriceDataProvider) {
        addClassName("order-form");

        content = new VerticalLayout();
        content.setSizeUndefined();
        content.addClassName("order-form-content");
        add(content);

        setupTickerComboBox(stockPriceDataProvider);
        setupFields();

        Component buttonLayout = buttonLayout(saveButton);

        setupBinder(stockPriceDataProvider);

        HorizontalLayout horizontalLayout = new HorizontalLayout(quantityField, rateField);
        horizontalLayout.setWidth("100%");
        horizontalLayout.setFlexGrow(1, quantityField, rateField);

        Stream.of(tickerComboBox, horizontalLayout, dateAtField, totalUsdField, commissionAmountField, buttonLayout)
                .forEach(content::add);
    }

    private void setupTickerComboBox(StockPriceDataProvider dataProvider) {
        tickerComboBox.setItems(dataProvider);
        tickerComboBox.setRequired(true);
        tickerComboBox.setAllowCustomValue(true);
        tickerComboBox.setAllowedCharPattern("[A-Z]");
        tickerComboBox.setItemLabelGenerator(StockPriceDto::getTicker);
        tickerComboBox.addCustomValueSetListener(e -> {
            String customValue = e.getDetail();
            dataProvider.saveNewTicker(customValue);
        });
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

    private void setupFields() {
        commissionAmountField.setSuffixComponent(VaadinIcon.DOLLAR.create());
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

    private void setupBinder(StockPriceDataProvider stockPriceDataProvider) {
        orderDtoBinder.bind(tickerComboBox,
                order -> stockPriceDataProvider.getByTicker(order.getTicker()),
                (orderViewModel, stockPriceDto) -> orderViewModel.setTicker(stockPriceDto.getTicker()));
        orderDtoBinder.forField(quantityField).bind(OrderViewModel::getQuantity, OrderViewModel::setQuantity);
        orderDtoBinder.forField(rateField).bind(OrderViewModel::getRate, OrderViewModel::setRate);
        orderDtoBinder.bind(dateAtField, OrderViewModel::getDateAt, OrderViewModel::setDateAt);
        orderDtoBinder.forField(totalUsdField).bind(OrderViewModel::getTotalUsd, OrderViewModel::setTotalUsd);
        orderDtoBinder.forField(commissionAmountField).bind(OrderViewModel::getCommissionAmount, OrderViewModel::setCommissionAmount);
        orderDtoBinder.bind(commentField, OrderViewModel::getComment, OrderViewModel::setComment);

        orderDtoBinder.addStatusChangeListener(event -> saveButton.setEnabled(orderDtoBinder.isValid()));
    }

    public void setOrder(UUID portfolioId, OrderViewModel orderDto) {
        orderDtoBinder.setBean(orderDto);
    }

    @Override
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return super.addListener(eventType, listener);
    }
}
