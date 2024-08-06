package org.dharbar.telegabot.view.events;

import com.vaadin.flow.component.ComponentEvent;
import lombok.Getter;
import org.dharbar.telegabot.service.positionmanagment.dto.OrderDto;
import org.dharbar.telegabot.view.OrderDetailsForm;

@Getter
public abstract class OrderFormEvent extends ComponentEvent<OrderDetailsForm> {
    private final OrderDto orderDto;

    public OrderFormEvent(OrderDetailsForm source, OrderDto orderDto) {
        super(source, false);
        this.orderDto = orderDto;
    }

    public static class SaveOrderEvent extends OrderFormEvent {
        public SaveOrderEvent(OrderDetailsForm source, OrderDto orderDto) {
            super(source, orderDto);
        }
    }

    public static class CloseEvent extends OrderFormEvent {
        public CloseEvent(OrderDetailsForm source, OrderDto orderDto) {
            super(source, orderDto);
        }
    }

    @Getter
    public static class SaveTickerEvent extends OrderFormEvent {
        private final String ticker;

        public SaveTickerEvent(OrderDetailsForm source, OrderDto orderDto, String ticker) {
            super(source, orderDto);
            this.ticker = ticker;
        }
    }

}
