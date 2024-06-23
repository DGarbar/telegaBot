package org.dharbar.telegabot.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.dharbar.telegabot.repository.entity.OrderType;
import org.dharbar.telegabot.service.trademanagment.TradeService;
import org.dharbar.telegabot.service.trademanagment.dto.OrderDto;
import org.dharbar.telegabot.service.trademanagment.dto.TradeDto;

import java.math.BigDecimal;
import java.util.stream.Stream;

public class TradeNewForm extends FormLayout {

    private final TextField tickerField = new TextField("Ticker");
    private final TextField buyQuantityField = new TextField("Buy quantity");
    private final TextField rateField = new TextField("Byt rate");
    private final DatePicker dateAtField = new DatePicker("Date at");
    private final TextField totalUsdField = new TextField("Total USD");
    private final TextField commissionUsdField = new TextField("Commission");
    private final TextField commentField = new TextField("Comment");
    private final Button saveButton;

    private final TradeService tradeService;

    public TradeNewForm(TradeService tradeService) {
        this.tradeService = tradeService;
        Stream.of(tickerField, buyQuantityField, rateField, dateAtField, totalUsdField, commissionUsdField)
                .forEach(this::add);


        // Binder<TradeDto> tradeDtoBinder = new Binder<>();
        // tradeDtoBinder.

        saveButton = new Button("Save");
        saveButton.addClickListener(e -> saveTrade());

        add(saveButton);
    }

    private void saveTrade() {
        OrderDto orderDto = OrderDto.builder()
                .type(OrderType.BUY)
                .ticker(tickerField.getValue())
                .quantity(new BigDecimal(buyQuantityField.getValue()))
                .rate(new BigDecimal(rateField.getValue()))
                .dateAt(dateAtField.getValue())
                .totalUsd(new BigDecimal(totalUsdField.getValue()))
                .commissionUsd(new BigDecimal(commissionUsdField.getValue()))
                .comment(commentField.getValue())
                .build();


        TradeDto tradeDto = TradeDto.builder()
                .byuOrder(orderDto)
                .comment(commentField.getValue())
                .build();

        tradeService.saveTrade(tradeDto);
    }


}
