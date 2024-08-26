package org.dharbar.telegabot.view.positionview;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import lombok.extern.slf4j.Slf4j;
import org.dharbar.telegabot.repository.entity.OrderType;
import org.dharbar.telegabot.service.stockprice.dto.StockPriceDto;
import org.dharbar.telegabot.view.model.OrderViewModel;
import org.dharbar.telegabot.view.model.PositionViewModel;
import org.dharbar.telegabot.view.positionview.ticker.StockPriceDataProvider;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
public class PositionForm extends Div {

    private final VerticalLayout content;

    private final TextField positionCommentField = new TextField("Comment");

    private final ComboBox<StockPriceDto> tickerComboBox = new ComboBox<>("Ticker");
    private final BigDecimalField quantityField = new BigDecimalField("Quantity");
    private final BigDecimalField rateField = new BigDecimalField("Rate");
    private final DatePicker dateAtField = new DatePicker("Date at");
    private final BigDecimalField totalUsdField = new BigDecimalField("Total");
    private final BigDecimalField commissionAmountField = new BigDecimalField("Commission");

    private final Binder<PositionViewModel> positionViewBinder = new Binder<>(PositionViewModel.class);
    private final Binder<OrderViewModel> orderViewBinder = new Binder<>(OrderViewModel.class);

    private final Button saveButton = new Button("Save");

    private final PositionDataProvider positionDataProvider;

    public PositionForm(PositionDataProvider positionDataProvider,
                        StockPriceDataProvider stockPriceDataProvider) {
        this.positionDataProvider = positionDataProvider;

        addClassName("right-form");

        content = new VerticalLayout();
        content.setSizeUndefined();
        content.addClassName("right-form-content");
        add(content);

        setupTickerComboBox(stockPriceDataProvider);
        setupFields();

        Component buttonLayout = buttonLayout(saveButton);

        setupBinder(stockPriceDataProvider);

        HorizontalLayout horizontalLayout = new HorizontalLayout(quantityField, rateField);
        horizontalLayout.setWidth("100%");
        horizontalLayout.setFlexGrow(1, quantityField, rateField);

        Stream.of(tickerComboBox, positionCommentField, horizontalLayout, dateAtField, totalUsdField, commissionAmountField, buttonLayout)
                .forEach(content::add);

        showForm(false);
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

    private void setupFields() {
        rateField.setSuffixComponent(VaadinIcon.DOLLAR.create());
        rateField.addValueChangeListener(e -> recalculateTotalField());
        quantityField.addValueChangeListener(e -> recalculateTotalField());
        totalUsdField.setSuffixComponent(VaadinIcon.DOLLAR.create());
        commissionAmountField.setSuffixComponent(VaadinIcon.DOLLAR.create());
    }

    private Component buttonLayout(Button saveButton) {
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickShortcut(Key.ENTER);
        saveButton.addClickListener(e -> processPositionOrderCreation());

        Button closeButton = new Button("Close");
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        closeButton.addClickShortcut(Key.ESCAPE);

        closeButton.addClickListener(e -> showForm(false));

        return new HorizontalLayout(saveButton, closeButton);
    }

    private void processPositionOrderCreation() {
        // if (positionViewBinder.isValid()) {
        PositionViewModel position = positionViewBinder.getBean();
        OrderViewModel order = orderViewBinder.getBean();

        boolean isNewPosition = position.getId() == null;
        if (isNewPosition) {
            position.getOrders().add(order);
            position.setTicker(order.getTicker());
            positionDataProvider.saveNewPosition(position);

            Notification.show(order.getTicker() + " created");

        } else if (order.getId() == null) {
            positionDataProvider.addOrderToPosition(position, order);
            Notification.show(order.getTicker() + " updated");
        }

        showForm(false);
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
        positionViewBinder.bind(positionCommentField, PositionViewModel::getComment, PositionViewModel::setComment);

        orderViewBinder.bind(tickerComboBox,
                order -> stockPriceDataProvider.getByTicker(order.getTicker()),
                (order, stockPriceDto) -> order.setTicker(stockPriceDto.getTicker()));
        orderViewBinder.forField(quantityField).bind(OrderViewModel::getQuantity, OrderViewModel::setQuantity);
        orderViewBinder.forField(rateField).bind(OrderViewModel::getRate, OrderViewModel::setRate);
        orderViewBinder.bind(dateAtField, OrderViewModel::getDateAt, OrderViewModel::setDateAt);
        orderViewBinder.forField(totalUsdField).bind(OrderViewModel::getTotalUsd, OrderViewModel::setTotalUsd);
        orderViewBinder.forField(commissionAmountField).bind(OrderViewModel::getCommissionAmount, OrderViewModel::setCommissionAmount);

        orderViewBinder.addStatusChangeListener(event -> saveButton.setEnabled(orderViewBinder.isValid()));
    }

    public void showNewPosition(UUID portfolioId) {
        showForm(true);

        PositionViewModel position = PositionViewModel.builder()
                .orders(new ArrayList<>())
                .portfolioId(portfolioId)
                .build();

        OrderViewModel order = OrderViewModel.builder()
                .type(OrderType.BUY)
                .commissionAmount(BigDecimal.ZERO)
                .dateAt(LocalDate.now())
                .build();

        positionViewBinder.setBean(position);
        orderViewBinder.setBean(order);
    }

    public void showPositionBuyOrder(PositionViewModel position) {
        showForm(true);

        OrderViewModel order = OrderViewModel.builder()
                .positionId(position.getId())
                .type(OrderType.BUY)
                .ticker(position.getTicker())
                .rate(position.getCurrentRatePrice())
                .commissionAmount(BigDecimal.ZERO)
                .dateAt(LocalDate.now())
                .build();

        positionViewBinder.setBean(position);
        orderViewBinder.setBean(order);
    }

    public void showSellAllOrder(PositionViewModel position) {
        showForm(true);

        OrderViewModel order = OrderViewModel.builder()
                .positionId(position.getId())
                .type(OrderType.SELL)
                .ticker(position.getTicker())
                .rate(position.getCurrentRatePrice())
                .quantity(position.getBuyQuantity().subtract(position.getSellQuantity()))
                .commissionAmount(BigDecimal.ZERO)
                .dateAt(LocalDate.now())
                .build();

        positionViewBinder.setBean(position);
        orderViewBinder.setBean(order);
    }

    private void showForm(boolean show) {
        setVisible(show);
        setEnabled(show);
    }
}
