package org.dharbar.telegabot.view.positionview;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.dharbar.telegabot.controller.PositionController;
import org.dharbar.telegabot.controller.StockPriceController;
import org.dharbar.telegabot.service.stockprice.dto.StockPriceDto;
import org.dharbar.telegabot.view.MainLayout;
import org.dharbar.telegabot.view.events.OrderFormEvent;
import org.dharbar.telegabot.view.mapper.PositionViewMapper;
import org.dharbar.telegabot.view.model.OrderViewModel;
import org.dharbar.telegabot.view.model.PositionViewModel;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Route(value = "positions", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@CssImport(value = "./styles/grid.css", themeFor = "vaadin-grid")
public class PositionView extends HorizontalLayout implements HasUrlParameter<String> {

    public static final String VIEW_NAME = "Positions";

    private final Grid<PositionViewModel> grid;
    private final OrderCreationForm orderDetailsForm;
    private final Checkbox isOnlyOpenCheckbox = new Checkbox("Open only", true);

    private final PositionViewLogic viewLogic;
    private final Map<String, BigDecimal> tickerToPrice;
    private final PositionDataProvider positionDataProvider;
    private final StockPriceController stockPriceController;

    private TextField filter;
    private Button newPostitionButton;

    public PositionView(StockPriceController stockPriceController,
                        PositionController positionController,
                        PositionViewMapper positionViewMapper) {
        addClassName("position-view");
        setSizeFull();

        this.stockPriceController = stockPriceController;
        positionDataProvider = new PositionDataProvider(positionController, positionViewMapper, isOnlyOpenCheckbox);
        viewLogic = new PositionViewLogic(this, positionDataProvider);

        HorizontalLayout topLayout = setupToolbar();

        grid = new PositionGrid(viewLogic);
        grid.setDataProvider(positionDataProvider);
        // Allows user to select a single row in the grid.
        // grid.asSingleSelect().addValueChangeListener(event -> viewLogic.rowSelected(event.getValue()));

        tickerToPrice = stockPriceController.getStockPrices().stream()
                .collect(Collectors.toMap(StockPriceDto::getTicker, StockPriceDto::getPrice));
        Set<String> tickers = tickerToPrice.keySet();
        orderDetailsForm = new OrderCreationForm(tickers);
        orderDetailsForm.addListener(OrderFormEvent.SaveOrderEvent.class, viewLogic::saveOrder);
        orderDetailsForm.addListener(OrderFormEvent.CloseEvent.class, e -> viewLogic.closeEditor());
        orderDetailsForm.addListener(OrderFormEvent.SaveTickerEvent.class, this::addNewTicker);


        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.add(topLayout);
        barAndGridLayout.add(grid);
        barAndGridLayout.setFlexGrow(1, grid);
        barAndGridLayout.setFlexGrow(0, topLayout);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.expand(grid);

        add(barAndGridLayout);
        add(orderDetailsForm);

        listPositions();
        showForm(false);
    }

    private HorizontalLayout setupToolbar() {
        filter = new TextField();
        filter.setPlaceholder("(TODO) Filter name, availability or category");
        // Apply the filter to grid's data provider. TextField value is never
        // TODO ?
        // filter.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));
        // A shortcut to focus on the textField by pressing ctrl + F
        filter.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);

        newPostitionButton = new Button("New position");
        // Setting theme variant of new production button to LUMO_PRIMARY that
        // changes its background color to blue and its text color to white
        newPostitionButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newPostitionButton.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        newPostitionButton.addClickListener(click -> viewLogic.viewNewBuyOrder());
        // A shortcut to click the new product button by pressing ALT + N
        newPostitionButton.addClickShortcut(Key.KEY_N, KeyModifier.ALT);

        isOnlyOpenCheckbox.setValue(true);
        isOnlyOpenCheckbox.addValueChangeListener(e -> listPositions());

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(isOnlyOpenCheckbox);
        topLayout.add(filter);
        topLayout.add(newPostitionButton);
        topLayout.setVerticalComponentAlignment(Alignment.START, filter);
        topLayout.expand(filter);
        return topLayout;
    }

    private void listPositions() {
        Boolean isOnlyOpen = isOnlyOpenCheckbox.getValue();
        if (isOnlyOpen) {
            grid.getColumnByKey("sellRate").setVisible(false);
            grid.getColumnByKey("netProfitAmount").setVisible(false);
            grid.getColumnByKey("profitPercentage").setVisible(false);
        } else {
            grid.getColumnByKey("sellRate").setVisible(true);
            grid.getColumnByKey("netProfitAmount").setVisible(true);
            grid.getColumnByKey("profitPercentage").setVisible(true);
        }
        positionDataProvider.refreshAll();
    }

    public void addNewTicker(OrderFormEvent.SaveTickerEvent event) {
        String ticker = event.getTicker();
        try {
            StockPriceDto stockPrice = stockPriceController.createStockPrice(ticker);
            tickerToPrice.put(ticker, stockPrice.getPrice());
        } catch (Exception e) {
            Notification notification = new Notification("Failed to add new ticker " + ticker, 5000, Notification.Position.TOP_END);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return;
        }

        orderDetailsForm.setTickerValues(tickerToPrice.keySet(), ticker);
    }

    // public void selectRow(PositionViewModel row) {
    //     grid.getSelectionModel().select(row);
    // }

    public void showOrderForm(OrderViewModel order) {
        if (order != null && order.getId() == null) {
            clearSelection();
        }

        orderDetailsForm.setOrder(order);
        showForm(true);
    }

    public void clearSelection() {
        grid.getSelectionModel().deselectAll();
    }

    public void showForm(boolean show) {
        orderDetailsForm.setVisible(show);
        orderDetailsForm.setEnabled(show);
    }

    public void showNotification(String msg) {
        Notification.show(msg);
    }

    @Override
    public void setParameter(BeforeEvent event,
                             @OptionalParameter String parameter) {
        // viewLogic.enter(parameter);
    }
}
