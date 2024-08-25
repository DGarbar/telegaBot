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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.dharbar.telegabot.controller.PortfolioController;
import org.dharbar.telegabot.controller.PositionController;
import org.dharbar.telegabot.controller.StockPriceController;
import org.dharbar.telegabot.view.MainLayout;
import org.dharbar.telegabot.view.events.OrderFormEvent;
import org.dharbar.telegabot.view.mapper.PortfolioViewMapper;
import org.dharbar.telegabot.view.mapper.PositionViewMapper;
import org.dharbar.telegabot.view.model.OrderViewModel;
import org.dharbar.telegabot.view.model.PortfolioViewModel;
import org.dharbar.telegabot.view.model.PositionViewModel;
import org.dharbar.telegabot.view.positionview.portfolio.PortfolioCreationDialog;
import org.dharbar.telegabot.view.positionview.portfolio.PortfolioDataProvider;
import org.dharbar.telegabot.view.positionview.ticker.StockPriceDataProvider;

import java.util.UUID;

@Route(value = "positions", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@CssImport(value = "./styles/grid.css", themeFor = "vaadin-grid")
public class PositionView extends HorizontalLayout implements HasUrlParameter<String> {

    public static final String VIEW_NAME = "Positions";

    private final Grid<PositionViewModel> grid;
    private final OrderCreationForm orderDetailsForm;
    private final PortfolioCreationDialog portfolioCreationDialog;

    private final Checkbox isShowClosedCheckbox = new Checkbox("Show Closed", false);
    private final Select<PortfolioViewModel> portfolioSelect = new Select<>();

    private final PositionViewLogic viewLogic;

    private final PositionDataProvider positionDataProvider;
    private final PortfolioDataProvider portfolioDataProvider;
    private final StockPriceDataProvider stockPriceDataProvider;

    private TextField filter;
    private Button newPostitionButton;

    public PositionView(StockPriceController stockPriceController,
                        PortfolioController portfolioController,
                        PortfolioViewMapper portfolioViewMapper,
                        PositionController positionController,
                        PositionViewMapper positionViewMapper) {
        addClassName("position-view");
        setSizeFull();

        positionDataProvider = new PositionDataProvider(positionController, positionViewMapper, portfolioSelect, isShowClosedCheckbox);
        portfolioDataProvider = new PortfolioDataProvider(portfolioController, portfolioViewMapper);
        stockPriceDataProvider = new StockPriceDataProvider(stockPriceController);
        viewLogic = new PositionViewLogic(this, positionDataProvider);

        portfolioCreationDialog = new PortfolioCreationDialog(portfolioDataProvider);

        HorizontalLayout gridToolbar = setupGridToolbar(portfolioDataProvider);

        grid = new PositionGrid(viewLogic);
        grid.setDataProvider(positionDataProvider);
        // Allows user to select a single row in the grid.
        // grid.asSingleSelect().addValueChangeListener(event -> viewLogic.rowSelected(event.getValue()));

        orderDetailsForm = new OrderCreationForm(stockPriceDataProvider);
        orderDetailsForm.addListener(OrderFormEvent.SaveOrderEvent.class, viewLogic::saveOrder);
        orderDetailsForm.addListener(OrderFormEvent.CloseEvent.class, e -> viewLogic.closeEditor());

        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.add(gridToolbar);
        barAndGridLayout.add(grid);
        barAndGridLayout.setFlexGrow(0, gridToolbar);
        barAndGridLayout.setFlexGrow(1, grid);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.expand(grid);

        add(barAndGridLayout);
        add(orderDetailsForm);

        listPositions();
        showForm(false);
    }

    private HorizontalLayout setupGridToolbar(PortfolioDataProvider portfolioDataProvider) {
        isShowClosedCheckbox.addValueChangeListener(e -> listPositions());

        portfolioSelect.setEmptySelectionAllowed(true);
        portfolioSelect.setEmptySelectionCaption("All Portfolios");
        portfolioSelect.setItemLabelGenerator(item -> item != null ? item.getName() : "All Portfolios");
        portfolioSelect.setDataProvider(portfolioDataProvider);
        portfolioSelect.addValueChangeListener(e -> {
            newPostitionButton.setEnabled(e.getValue() != null);
            listPositions();
        });

        Button addPortfolioButton = new Button("Add Portfolio", VaadinIcon.PLUS_CIRCLE.create());
        addPortfolioButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addPortfolioButton.addClickListener(e -> portfolioCreationDialog.open());

        filter = new TextField();
        filter.setPlaceholder("(TODO) Filter name, availability or category");
        // Apply the filter to grid's data provider. TextField value is never
        // TODO ?
        // filter.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));
        // A shortcut to focus on the textField by pressing ctrl + F
        filter.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);

        newPostitionButton = new Button("New position", VaadinIcon.PLUS_CIRCLE.create());
        newPostitionButton.setEnabled(false);
        // Setting theme variant of new production button to LUMO_PRIMARY that
        // changes its background color to blue and its text color to white
        newPostitionButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newPostitionButton.addClickListener(click -> viewLogic.viewNewBuyOrder());
        // A shortcut to click the new product button by pressing ALT + N
        newPostitionButton.addClickShortcut(Key.KEY_N, KeyModifier.ALT);

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(portfolioSelect);
        topLayout.add(addPortfolioButton);
        topLayout.add(isShowClosedCheckbox);
        topLayout.add(filter);
        topLayout.add(newPostitionButton);
        topLayout.setVerticalComponentAlignment(Alignment.START, filter);
        topLayout.expand(filter);
        return topLayout;
    }

    private void listPositions() {
        Boolean isShowClosed = isShowClosedCheckbox.getValue();
        if (isShowClosed) {
            grid.getColumnByKey("sellRate").setVisible(true);
            grid.getColumnByKey("netProfitAmount").setVisible(true);
            grid.getColumnByKey("profitPercentage").setVisible(true);
        } else {
            grid.getColumnByKey("sellRate").setVisible(false);
            grid.getColumnByKey("netProfitAmount").setVisible(false);
            grid.getColumnByKey("profitPercentage").setVisible(false);
        }
        positionDataProvider.refreshAll();
    }

    // public void selectRow(PositionViewModel row) {
    //     grid.getSelectionModel().select(row);
    // }

    public UUID getSelectedPortfolioId() {
        return portfolioSelect.getValue() != null ? portfolioSelect.getValue().getId() : null;
    }

    public void showOrderForm(UUID portfolioId, OrderViewModel order) {
        if (order != null && order.getId() == null) {
            clearSelection();
        }

        orderDetailsForm.setOrder(portfolioId, order);
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
