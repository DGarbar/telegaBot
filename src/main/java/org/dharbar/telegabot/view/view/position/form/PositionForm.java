package org.dharbar.telegabot.view.view.position.form;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import lombok.extern.slf4j.Slf4j;
import org.dharbar.telegabot.repository.entity.PositionType;
import org.dharbar.telegabot.service.ticker.dto.TickerDto;
import org.dharbar.telegabot.view.model.PositionViewModel;
import org.dharbar.telegabot.view.view.alarm.AlarmListCustomField;
import org.dharbar.telegabot.view.view.order.OrderDialog;
import org.dharbar.telegabot.view.view.position.PositionDataProvider;
import org.dharbar.telegabot.view.view.ticker.TickerCreationDialog;
import org.dharbar.telegabot.view.view.ticker.TickerDataProvider;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
public class PositionForm extends Div {

    private final VerticalLayout content;

    private final TextField nameField = new TextField("Name");
    private final TextArea commentArea = new TextArea("Comment");

    private final ComboBox<TickerDto> tickerComboBox = new ComboBox<>("Ticker");
    private final Button addTickerButton = new Button(VaadinIcon.PLUS.create());

    private final ComboBox<PositionType> positionTypeComboBox = new ComboBox<>("Type");

    private final PriceTriggersCustomField priceTriggersCustomField = new PriceTriggersCustomField();
    private final AlarmListCustomField alarmListComponent;

    private final Binder<PositionViewModel> positionViewBinder = new Binder<>(PositionViewModel.class);

    private final Button saveButton = new Button("Save Position");
    private final Button addOrderButton = new Button("Add Order", VaadinIcon.DOLLAR.create());
    private final Button sellAllOrderButton = new Button("Sell All Order", VaadinIcon.CLOSE_CIRCLE.create());
    private final Button recalcualtePositionButton = new Button("Recalc.", VaadinIcon.REFRESH.create());

    private final PositionDataProvider positionDataProvider;
    private final OrderDialog orderDialog;

    public PositionForm(PositionDataProvider positionDataProvider,
                        TickerDataProvider tickerDataProvider) {
        this.positionDataProvider = positionDataProvider;
        alarmListComponent = new AlarmListCustomField(positionDataProvider);

        addClassName("right-form");

        content = new VerticalLayout();
        content.setSizeUndefined();
        content.addClassName("right-form-content");
        add(content);

        TickerCreationDialog tickerCreationDialog = new TickerCreationDialog(tickerDataProvider);
        Component tickerLayout = setupTickerLayoutComboBox(tickerDataProvider, tickerCreationDialog);
        setupPositionTypeComboBox();
        // Component priceSettingsLayout = setupPriceSettings();

        Component orderButtonLayout = setupOrderButtonLayout();
        Component positionButtonLayout = setupFunctionalButtonLayout();

        setupBinder(tickerDataProvider);

        orderDialog = new OrderDialog();

        Stream.of(nameField,
                        tickerLayout,
                        positionTypeComboBox,
                        commentArea,
                        priceTriggersCustomField,
                        alarmListComponent,
                        orderButtonLayout,
                        positionButtonLayout)
                .forEach(content::add);

        showForm(false);
    }

    private Component setupTickerLayoutComboBox(TickerDataProvider dataProvider, TickerCreationDialog tickerCreationDialog) {
        tickerComboBox.setItems(dataProvider.getItems());
        tickerComboBox.setRequired(true);
        tickerComboBox.setAllowedCharPattern("[A-Z]");
        tickerComboBox.setItemLabelGenerator(TickerDto::getTicker);
        tickerComboBox.addValueChangeListener(e -> {
            TickerDto value = e.getValue();
            if (value != null && nameField.isEmpty()) {
                nameField.setValue(value.getTicker());
            }
        });

        addTickerButton.addClickListener(event ->
                tickerCreationDialog.open(tickerDto -> {
                    tickerComboBox.setItems(dataProvider.getItems());
                    tickerComboBox.setValue(tickerDto);
                }));

        HorizontalLayout horizontalLayout = new HorizontalLayout(tickerComboBox, addTickerButton);
        horizontalLayout.setVerticalComponentAlignment(FlexComponent.Alignment.END, addTickerButton);

        return horizontalLayout;
    }

    private void setupPositionTypeComboBox() {
        positionTypeComboBox.setItems(PositionType.values());
        positionTypeComboBox.setValue(PositionType.SIMPLE);
        positionTypeComboBox.setRequired(true);
        positionTypeComboBox.setAllowCustomValue(false);
        positionTypeComboBox.setItemLabelGenerator(PositionType::name);

        positionTypeComboBox.addValueChangeListener(event -> {
            PositionType positionType = event.getValue();
            priceTriggersCustomField.showGridSettings(positionType == PositionType.GRID);
        });
    }

    private Component setupOrderButtonLayout() {
        addOrderButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        addOrderButton.setIcon(VaadinIcon.DOLLAR.create());
        addOrderButton.addClickListener(e -> {
            PositionViewModel position = positionViewBinder.getBean();
            orderDialog.showNewOrder(position.getTicker(), BigDecimal.ZERO, position.getId(), order -> position.getOrders().add(order));
        });

        sellAllOrderButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        sellAllOrderButton.addClickListener(e -> {
            PositionViewModel position = positionViewBinder.getBean();
            openSellAllOrder(position);
        });
        sellAllOrderButton.setIcon(VaadinIcon.CLOSE_CIRCLE.create());

        return new HorizontalLayout(addOrderButton, sellAllOrderButton);
    }

    private Component setupFunctionalButtonLayout() {
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickShortcut(Key.ENTER);
        saveButton.addClickListener(e -> processPositionOrderCreation());

        recalcualtePositionButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        recalcualtePositionButton.addClickListener(e -> {
            UUID id = positionViewBinder.getBean().getId();
            positionDataProvider.recalculatePosition(id);
        });

        Button closeButton = new Button("Close");
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        closeButton.addClickShortcut(Key.ESCAPE);

        closeButton.addClickListener(e -> showForm(false));

        return new HorizontalLayout(saveButton, recalcualtePositionButton, closeButton);
    }

    private void processPositionOrderCreation() {
        PositionViewModel position = positionViewBinder.getBean();

        boolean isNewPosition = position.getId() == null;
        if (isNewPosition) {
            positionDataProvider.saveNewPosition(position);
            Notification.show(position.getTicker() + " created").setPosition(Notification.Position.TOP_END);
        } else {
            positionDataProvider.updatePosition(position);
            Notification.show(position.getTicker() + " updated").setPosition(Notification.Position.TOP_END);
        }

        showForm(false);
    }

    private void setupBinder(TickerDataProvider tickerDataProvider) {
        positionViewBinder.bind(nameField, PositionViewModel::getName, PositionViewModel::setName);
        positionViewBinder.bind(tickerComboBox,
                position -> tickerDataProvider.getByTicker(position.getTicker()),
                (position, tickerDto) -> position.setTicker(tickerDto.getTicker()));

        positionViewBinder.bind(positionTypeComboBox, PositionViewModel::getType, PositionViewModel::setType);
        positionViewBinder.bind(commentArea, PositionViewModel::getComment, PositionViewModel::setComment);

        positionViewBinder.bind(priceTriggersCustomField, PositionViewModel::getPriceTriggers, PositionViewModel::setPriceTriggers);

        positionViewBinder.bind(alarmListComponent, PositionViewModel::getAlarms, PositionViewModel::setAlarms);
    }

    public void showNewPosition(UUID portfolioId) {
        showForm(true);

        PositionViewModel position = PositionViewModel.builder()
                .orders(new HashSet<>())
                .priceTriggers(new HashSet<>())
                .portfolioId(portfolioId)
                .type(PositionType.SIMPLE)
                .build();
        positionViewBinder.setBean(position);
    }

    public void showPosition(PositionViewModel position) {
        showForm(true);
        positionViewBinder.setBean(position);
    }

    public void openSellAllOrder(PositionViewModel position) {
        BigDecimal quantity = position.getBuyQuantity().subtract(position.getSellQuantity());
        orderDialog.showSellAllOrder(position.getTicker(),
                position.getCurrentRatePrice(),
                quantity,
                position.getId(),
                order -> position.getOrders().add(order));
    }

    private void showForm(boolean show) {
        setVisible(show);
        setEnabled(show);
    }
}
