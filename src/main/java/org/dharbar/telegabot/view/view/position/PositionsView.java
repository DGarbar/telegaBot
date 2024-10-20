package org.dharbar.telegabot.view.view.position;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.dharbar.telegabot.controller.PortfolioController;
import org.dharbar.telegabot.controller.PositionController;
import org.dharbar.telegabot.controller.TickerController;
import org.dharbar.telegabot.view.MainLayout;
import org.dharbar.telegabot.view.mapper.PortfolioViewMapper;
import org.dharbar.telegabot.view.mapper.PositionViewMapper;
import org.dharbar.telegabot.view.mapper.TickerViewMapper;
import org.dharbar.telegabot.view.model.PortfolioViewModel;
import org.dharbar.telegabot.view.model.PositionViewModel;
import org.dharbar.telegabot.view.view.portfolio.PortfolioDataProvider;
import org.dharbar.telegabot.view.view.portfolio.PortfolioDialog;
import org.dharbar.telegabot.view.view.position.form.PositionForm;
import org.dharbar.telegabot.view.view.ticker.TickerDataProvider;

import java.util.UUID;

@Route(value = "positions", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@CssImport(value = "./styles/grid.css", themeFor = "vaadin-grid")
public class PositionsView extends HorizontalLayout {

    public static final String VIEW_NAME = "Positions";

    private final Grid<PositionViewModel> grid;
    private final PositionForm positionForm;
    private final PortfolioDialog portfolioCreationDialog;

    private final Checkbox isShowClosedCheckbox = new Checkbox("Show Closed", false);
    private final Select<PortfolioViewModel> portfolioSelect = new Select<>();
    private final Binder<PortfolioViewModel> portfolioBinder = new Binder<>(PortfolioViewModel.class);

    private final PositionDataProvider positionDataProvider;
    private final PortfolioDataProvider portfolioDataProvider;
    private final TickerDataProvider tickerDataProvider;

    private TextField filter;
    private final Button portfolioButton = new Button("Add Portfolio", VaadinIcon.PLUS_CIRCLE.create());
    private final Button newPostitionButton = new Button("New position", VaadinIcon.PLUS_CIRCLE.create());
    private final Accordion portfolioInfoTab = new Accordion();

    public PositionsView(TickerController tickerController,
                         TickerViewMapper tickerViewMapper,
                         PortfolioController portfolioController,
                         PortfolioViewMapper portfolioViewMapper,
                         PositionController positionController,
                         PositionViewMapper positionViewMapper) {
        addClassName("position-view");
        setSizeFull();

        positionDataProvider = new PositionDataProvider(positionController, positionViewMapper, portfolioSelect, isShowClosedCheckbox);
        portfolioDataProvider = new PortfolioDataProvider(portfolioController, portfolioViewMapper);
        tickerDataProvider = new TickerDataProvider(tickerController, tickerViewMapper);

        portfolioCreationDialog = new PortfolioDialog(portfolioDataProvider, portfolioBinder);

        HorizontalLayout gridToolbar = setupGridToolbar(portfolioDataProvider);
        Accordion portfolioInfo = setupPortfolioInfo(portfolioBinder);

        positionForm = new PositionForm(positionDataProvider, tickerDataProvider);

        grid = new PositionGrid(positionForm, positionDataProvider);
        grid.setDataProvider(positionDataProvider);
        // Allows user to select a single row in the grid.
        // grid.asSingleSelect().addValueChangeListener(event -> viewLogic.rowSelected(event.getValue()));

        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.add(gridToolbar);
        barAndGridLayout.add(portfolioInfo);
        barAndGridLayout.add(grid);
        barAndGridLayout.setFlexGrow(0, gridToolbar);
        barAndGridLayout.setFlexGrow(1, grid);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.expand(grid);

        add(barAndGridLayout);
        add(positionForm);

        listPositions();
    }

    private HorizontalLayout setupGridToolbar(PortfolioDataProvider portfolioDataProvider) {
        isShowClosedCheckbox.addValueChangeListener(e -> listPositions());

        portfolioSelect.setEmptySelectionAllowed(true);
        portfolioSelect.setEmptySelectionCaption("All Portfolios");
        portfolioSelect.setItemLabelGenerator(item -> item != null ? item.getName() : "All Portfolios");
        portfolioSelect.setDataProvider(portfolioDataProvider);
        portfolioSelect.addValueChangeListener(e -> {
            portfolioBinder.setBean(e.getValue());
            if (e.getValue() != null) {
                portfolioButton.setText("Edit Portfolio");
                portfolioButton.setIcon(VaadinIcon.EDIT.create());
                newPostitionButton.setEnabled(true);

                portfolioInfoTab.setVisible(true);
                portfolioInfoTab.getChildren().forEach(component -> component.setVisible(true));
            } else {
                portfolioButton.setText("Add Portfolio");
                portfolioButton.setIcon(VaadinIcon.PLUS_CIRCLE.create());
                newPostitionButton.setEnabled(false);

                portfolioInfoTab.setVisible(false);
                portfolioInfoTab.getChildren().forEach(component -> component.setVisible(false));
            }
            listPositions();
        });

        portfolioButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        portfolioButton.addClickListener(click -> portfolioCreationDialog.open());

        filter = new TextField();
        filter.setPlaceholder("(TODO) Filter name, availability or category");

        newPostitionButton.setEnabled(false);
        newPostitionButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newPostitionButton.addClickListener(click -> positionForm.showNewPosition(getSelectedPortfolioId()));

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(portfolioSelect);
        topLayout.add(portfolioButton);
        topLayout.add(isShowClosedCheckbox);
        topLayout.add(filter);
        topLayout.add(newPostitionButton);
        topLayout.setVerticalComponentAlignment(Alignment.START, filter);
        topLayout.expand(filter);

        return topLayout;
    }

    private Accordion setupPortfolioInfo(Binder<PortfolioViewModel> portfolioBinder) {
        portfolioInfoTab.close();
        portfolioInfoTab.setWidth("100%");
        portfolioInfoTab.setVisible(false);

        TextArea description = new TextArea();
        description.setWidthFull();
        description.setReadOnly(true);
        // TODO add update button to edit description
        portfolioBinder.bind(description, PortfolioViewModel::getDescription, PortfolioViewModel::setDescription);

        HorizontalLayout infoLayout = new HorizontalLayout(description);
        portfolioInfoTab.add("Portfolio Info", infoLayout);

        return portfolioInfoTab;
    }

    private void listPositions() {
        Boolean isShowClosed = isShowClosedCheckbox.getValue();
        if (isShowClosed) {
            grid.getColumnByKey("sellRate").setVisible(true);
            grid.getColumnByKey("profit").setVisible(true);
        } else {
            grid.getColumnByKey("sellRate").setVisible(false);
            grid.getColumnByKey("profit").setVisible(false);
        }
        positionDataProvider.refreshAll();
    }

    public UUID getSelectedPortfolioId() {
        return portfolioSelect.getValue() != null ? portfolioSelect.getValue().getId() : null;
    }

    public void clearSelection() {
        grid.getSelectionModel().deselectAll();
    }
}
