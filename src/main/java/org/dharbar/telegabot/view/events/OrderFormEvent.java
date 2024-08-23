package org.dharbar.telegabot.view.events;

import com.vaadin.flow.component.ComponentEvent;
import lombok.Getter;
import org.dharbar.telegabot.view.model.OrderViewModel;
import org.dharbar.telegabot.view.positionview.OrderCreationForm;

@Getter
public abstract class OrderFormEvent extends ComponentEvent<OrderCreationForm> {
    private final OrderViewModel order;

    public OrderFormEvent(OrderCreationForm source, OrderViewModel order) {
        super(source, false);
        this.order = order;
    }

    public static class SaveOrderEvent extends OrderFormEvent {
        public SaveOrderEvent(OrderCreationForm source, OrderViewModel order) {
            super(source, order);
        }
    }

    public static class CloseEvent extends OrderFormEvent {
        public CloseEvent(OrderCreationForm source, OrderViewModel order) {
            super(source, order);
        }
    }

    @Getter
    public static class SaveTickerEvent extends OrderFormEvent {
        private final String ticker;

        public SaveTickerEvent(OrderCreationForm source, OrderViewModel order, String ticker) {
            super(source, order);
            this.ticker = ticker;
        }
    }

}
