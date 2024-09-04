package org.dharbar.telegabot.view.view.order;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import lombok.extern.slf4j.Slf4j;
import org.dharbar.telegabot.repository.entity.OrderType;
import org.dharbar.telegabot.view.model.OrderViewModel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Slf4j
public class OrderDialog extends Dialog {

    private final VerticalLayout content;

    private final TextField tickerField = new TextField("Ticker");
    private final BigDecimalField quantityField = new BigDecimalField("Quantity");
    private final BigDecimalField rateField = new BigDecimalField("Rate");
    private final DatePicker dateAtField = new DatePicker("Date at");
    private final BigDecimalField totalUsdField = new BigDecimalField("Total");
    private final BigDecimalField commissionAmountField = new BigDecimalField("Commission");

    private final Binder<OrderViewModel> orderViewBinder = new Binder<>(OrderViewModel.class);

    private final Button saveButton = new Button("Save order");
    private final Button closeButton = new Button("Close");

    private Consumer<OrderViewModel> saveOrderConsumer;

    public OrderDialog() {
        setHeaderTitle("Add Order");

        content = new VerticalLayout();
        content.setSpacing(false);
        content.setPadding(false);
        content.setAlignItems(FlexComponent.Alignment.STRETCH);
        content.getStyle().set("width", "300px").set("max-width", "100%");
        add(content);

        setupFields();
        Component buttonLayout = setupFunctionalButtonLayout();

        setupBinder();

        HorizontalLayout horizontalLayout = new HorizontalLayout(quantityField, rateField);
        horizontalLayout.setWidth("100%");
        horizontalLayout.setFlexGrow(1, quantityField, rateField);

        Stream.of(tickerField, horizontalLayout, dateAtField, totalUsdField, commissionAmountField, buttonLayout)
                .forEach(content::add);
    }

    private void setupFields() {
        tickerField.setEnabled(false);

        rateField.setSuffixComponent(VaadinIcon.DOLLAR.create());
        rateField.addValueChangeListener(e -> recalculateTotalField());
        quantityField.addValueChangeListener(e -> recalculateTotalField());
        totalUsdField.setSuffixComponent(VaadinIcon.DOLLAR.create());
        commissionAmountField.setSuffixComponent(VaadinIcon.DOLLAR.create());
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

    private Component setupFunctionalButtonLayout() {
        saveButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        saveButton.addClickListener(e -> processOrderSave());

        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        closeButton.addClickShortcut(Key.ESCAPE);
        closeButton.addClickListener(e -> close());

        return new HorizontalLayout(saveButton, closeButton);
    }

    private void setupBinder() {
        orderViewBinder.bind(tickerField, OrderViewModel::getTicker, OrderViewModel::setTicker);
        orderViewBinder.forField(quantityField).bind(OrderViewModel::getQuantity, OrderViewModel::setQuantity);
        orderViewBinder.forField(rateField).bind(OrderViewModel::getRate, OrderViewModel::setRate);
        orderViewBinder.bind(dateAtField, OrderViewModel::getDateAt, OrderViewModel::setDateAt);
        orderViewBinder.forField(totalUsdField).bind(OrderViewModel::getTotalUsd, OrderViewModel::setTotalUsd);
        orderViewBinder.forField(commissionAmountField).bind(OrderViewModel::getCommissionAmount, OrderViewModel::setCommissionAmount);
    }

    private void processOrderSave() {
        saveOrderConsumer.accept(orderViewBinder.getBean());
        close();
    }

    public void showNewOrder(String ticker, BigDecimal price, UUID positionId, Consumer<OrderViewModel> saveOrderConsumer) {
        this.saveOrderConsumer = saveOrderConsumer;
        OrderViewModel order = OrderViewModel.builder()
                .positionId(positionId)
                .type(OrderType.BUY)
                .ticker(ticker)
                .rate(price)
                .commissionAmount(BigDecimal.ZERO)
                .dateAt(LocalDate.now())
                .build();

        orderViewBinder.setBean(order);

        open();
    }

    public void showSellAllOrder(String ticker, BigDecimal rate, BigDecimal quantity, UUID positionId, Consumer<OrderViewModel> saveOrderConsumer) {
        this.saveOrderConsumer = saveOrderConsumer;
        OrderViewModel order = OrderViewModel.builder()
                .positionId(positionId)
                .type(OrderType.SELL)
                .ticker(ticker)
                .rate(rate)
                .quantity(quantity)
                .commissionAmount(BigDecimal.ZERO)
                .dateAt(LocalDate.now())
                .build();

        orderViewBinder.setBean(order);

        open();
    }

    public void showEdit(OrderViewModel order, Consumer<OrderViewModel> saveOrderConsumer) {
        this.saveOrderConsumer = saveOrderConsumer;
        orderViewBinder.setBean(order);

        open();
    }
}
