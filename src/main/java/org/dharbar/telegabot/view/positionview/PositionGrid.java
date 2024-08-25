package org.dharbar.telegabot.view.positionview;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import org.dharbar.telegabot.view.model.PositionViewModel;
import org.dharbar.telegabot.view.utils.StyleUtils;

import java.text.DecimalFormat;
import java.util.List;

public class PositionGrid extends Grid<PositionViewModel> {

    public PositionGrid(PositionViewLogic viewLogic) {
        addClassName("position-grid");
        setSizeFull();

        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setMinimumFractionDigits(2);

        addColumn(PositionViewModel::getTicker).setHeader("Ticker").setKey("ticker").setFlexGrow(5)
                .setSortable(true)
                .setClassNameGenerator(positionAnalyticDto -> StyleUtils.toTickerStyle(positionAnalyticDto.getTicker()));
        Grid.Column<PositionViewModel> openAtColumn = addColumn(PositionViewModel::getOpenAt).setHeader("Date").setKey("openAt").setSortable(true).setFlexGrow(5);
        // TODO
        // product -> decimalFormat.format(product.getPrice()) + " $")
        addColumn(PositionViewModel::getBuyTotalAmount).setHeader("Invested").setTextAlign(ColumnTextAlign.END).setFlexGrow(3);
        addColumn(PositionViewModel::getBuyAveragePrice).setHeader("Buy Rate").setTextAlign(ColumnTextAlign.END).setFlexGrow(3);

        addColumn(PositionViewModel::getSellAveragePrice).setHeader("Sell Rate").setKey("sellRate").setTextAlign(ColumnTextAlign.END).setFlexGrow(3);
        addColumn(PositionViewModel::getNetProfitAmount).setHeader("Profit $").setKey("netProfitAmount").setTextAlign(ColumnTextAlign.END).setFlexGrow(3)
                .setClassNameGenerator(dto -> StyleUtils.toProfitStyle(dto.getProfitPercentage()));
        addColumn(PositionViewModel::getProfitPercentage).setHeader("Profit %").setKey("profitPercentage").setTextAlign(ColumnTextAlign.END).setFlexGrow(3)
                .setClassNameGenerator(dto -> StyleUtils.toProfitStyle(dto.getProfitPercentage()));

        addColumn(PositionViewModel::getCurrentRatePrice).setHeader("Current Rate").setTextAlign(ColumnTextAlign.END).setFlexGrow(3);
        addColumn(PositionViewModel::getCurrentNetProfitAmount).setHeader("Current Profit $").setTextAlign(ColumnTextAlign.END).setFlexGrow(3)
                .setClassNameGenerator(dto -> StyleUtils.toProfitStyle(dto.getCurrentProfitPercentage()));
        addColumn(PositionViewModel::getCurrentProfitPercentage).setHeader("Current Profit %").setTextAlign(ColumnTextAlign.END).setFlexGrow(3)
                .setClassNameGenerator(dto -> StyleUtils.toProfitStyle(dto.getCurrentProfitPercentage()));
        addColumn(PositionViewModel::dealDurationDays).setHeader("Deal days").setTextAlign(ColumnTextAlign.END).setFlexGrow(3);

        addColumn(PositionViewModel::getComment).setHeader("Comment").setFlexGrow(10);
        // getColumns().forEach(column -> column.setAutoWidth(true));

        addColumn(new ComponentRenderer<>(
                Button::new,
                (button, position) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                    button.addClickListener(e -> viewLogic.viewPositionBuyOrder(position));
                    button.setIcon(VaadinIcon.DOLLAR.create());
                    button.setEnabled(!position.getIsClosed());
                }))
                .setHeader("Add").setFlexGrow(5);

        addColumn(new ComponentRenderer<>(
                Button::new,
                (button, position) -> {
                    button.addThemeVariants(ButtonVariant.LUMO_ERROR);
                    button.addClickListener(e -> viewLogic.viewSellAllOrder(position));
                    button.setIcon(VaadinIcon.CLOSE_CIRCLE.create());
                    button.setEnabled(!position.getIsClosed());
                }))
                .setHeader("Sell All").setFlexGrow(5);

        // If the browser window size changes, check if all columns fit on screen
        // (e.g. switching from portrait to landscape mode)
        sort(List.of(new GridSortOrder<>(openAtColumn, SortDirection.DESCENDING)));

        // UI.getCurrent().getPage().addBrowserWindowResizeListener(
        //         e -> setColumnVisibility(e.getWidth()));
        // grid.setItemDetailsRenderer(new ComponentRenderer<>(PositionsDetailForm::new, PositionsDetailForm::setPositions));
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        // fetch browser width
        UI.getCurrent().getInternals().setExtendedClientDetails(null);
        // UI.getCurrent().getPage().retrieveExtendedClientDetails(e -> {
        //     setColumnVisibility(e.getBodyClientWidth());
        // });
    }

    public void refresh(PositionViewModel positionViewModel) {
        getDataCommunicator().refresh(positionViewModel);
    }

}


