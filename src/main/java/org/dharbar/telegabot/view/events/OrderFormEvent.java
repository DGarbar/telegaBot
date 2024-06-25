package org.dharbar.telegabot.view.events;

import com.vaadin.flow.component.ComponentEvent;
import lombok.Getter;
import org.dharbar.telegabot.service.trademanagment.dto.OrderDto;
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

}
