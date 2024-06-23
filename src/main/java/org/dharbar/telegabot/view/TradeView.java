package org.dharbar.telegabot.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import org.dharbar.telegabot.service.trademanagment.TradeService;
import org.dharbar.telegabot.service.trademanagment.dto.TradeDto;

@Route("trades")
public class TradeView extends VerticalLayout {

    private final Grid<TradeDto> grid;

    private final Button addTradeButton;
    // private final Button addNewButton;
    // private final Button saveButton;

    private final TradeService tradeService;

    public TradeView(TradeService tradeService) {
        this.tradeService = tradeService;

        addTradeButton = new Button("Add trade");
        addTradeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        // addTradeButton.addClickListener(e -> {
        // });

        this.grid = new Grid<>(TradeDto.class, false);
        grid.addColumn(TradeDto::getTicker).setHeader("Ticker");
        grid.addColumn(TradeDto::getBuyDateAt).setHeader("Date");
        grid.addColumn(TradeDto::getBuyTotalUsd).setHeader("Buy $");
        grid.addColumn(TradeDto::getBuyRate).setHeader("Buy Rate");
        grid.addColumn(TradeDto::getNetProfitUsd).setHeader("Profit $");
        grid.addColumn(TradeDto::getProfitPercentage).setHeader("Profit %");
        grid.addColumn(TradeDto::getComment).setHeader("Comment");

        grid.setItemDetailsRenderer(new ComponentRenderer<>(TradeDetailForm::new, TradeDetailForm::setTrade));


        TradeNewForm tradeNewForm = new TradeNewForm(tradeService);
        add(grid, tradeNewForm);
        listTrades();
    }

    private void addTrade() {

    }

    private void listTrades() {
        // TODO add filtering and pagination
        // grid.setItems(VaadinSpringDataHelpers.fromPagingRepository(repo))
        grid.setItems(tradeService.getTrades());
    }


    //  // Binder<TradeDto> binder = new Binder<>(TradeDto.class);
    //         // binder.bindInstanceFields(this);
    //         // TradeDto test = new TradeDto();
    //         // binder.readBean(test);
    //
    //
    //
    //         addNewButton = new Button("New trade", VaadinIcon.PLUS.create());
    //         saveButton  = new Button("Save trade", VaadinIcon.SAFE.create(),
    //                 event -> {
    //                     try {
    //                         binder.writeBean(test);
    //                         Notification.show(test.toString());
    //                     } catch (ValidationException e) {
    //                         throw new RuntimeException(e);
    //                     }
    //                 });

    // todo add filtering
    // by ticker
    // by completed


}
