package org.dharbar.telegabot.view.view.position;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import org.dharbar.telegabot.repository.entity.TriggerType;
import org.dharbar.telegabot.view.model.PositionViewModel;
import org.dharbar.telegabot.view.model.PriceTriggerViewModel;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class PriceTriggersCustomField extends CustomField<Set<PriceTriggerViewModel>> {

    private final BigDecimalField stopLossAmountField = new BigDecimalField("Stop Loss");
    private final BigDecimalField takeProfitTextField = new BigDecimalField("Take Profit");

    public PriceTriggersCustomField() {
        setLabel("Price Settings");
        setup();
    }

    public void setup() {
        stopLossAmountField.setSuffixComponent(VaadinIcon.DOLLAR.create());
        stopLossAmountField.setClearButtonVisible(true);
        stopLossAmountField.addValueChangeListener(event -> {
            BigDecimal value = event.getValue();
            if (value != null && !value.equals(BigDecimal.ZERO)) {
                addPositionPriceTrigger(TriggerType.STOP_LOSS, stopLossAmountField.getValue());
            } else {
                removePositionPriceTrigger(TriggerType.STOP_LOSS);
            }
        });

        takeProfitTextField.setSuffixComponent(VaadinIcon.DOLLAR.create());
        takeProfitTextField.setClearButtonVisible(true);
        takeProfitTextField.addValueChangeListener(event -> {
            BigDecimal value = event.getValue();
            if (value != null && !value.equals(BigDecimal.ZERO)) {
                addPositionPriceTrigger(TriggerType.TAKE_PROFIT, takeProfitTextField.getValue());
            } else {
                removePositionPriceTrigger(TriggerType.TAKE_PROFIT);
            }
        });

        HorizontalLayout horizontalLayout = new HorizontalLayout(stopLossAmountField, takeProfitTextField);
        add(horizontalLayout);
    }

    private void addPositionPriceTrigger(TriggerType triggerType, BigDecimal triggerPrice) {
        Set<PriceTriggerViewModel> priceTriggers = getValue();

        Optional<PriceTriggerViewModel> trigger = priceTriggers.stream()
                .filter(priceTrigger -> priceTrigger.getType().equals(triggerType))
                .findFirst();

        if (trigger.isPresent()) {
            trigger.get().setTriggerPrice(triggerPrice);
        } else {
            priceTriggers.add(PriceTriggerViewModel.builder()
                    .type(triggerType)
                    .triggerPrice(triggerPrice)
                    .build());
        }
    }

    private void removePositionPriceTrigger(TriggerType triggerType) {
        Set<PriceTriggerViewModel> priceTriggers = getValue();
        priceTriggers.removeIf(priceTrigger -> priceTrigger.getType().equals(triggerType));
    }

    @Override
    protected Set<PriceTriggerViewModel> generateModelValue() {
        return getValue();
    }

    @Override
    protected void setPresentationValue(Set<PriceTriggerViewModel> priceTriggerViewModels) {
        stopLossAmountField.clear();
        takeProfitTextField.clear();


        priceTriggerViewModels.forEach(priceTriggerViewModel -> {
            if (priceTriggerViewModel.getType().equals(TriggerType.STOP_LOSS)) {
                stopLossAmountField.setValue(priceTriggerViewModel.getTriggerPrice());
            } else if (priceTriggerViewModel.getType().equals(TriggerType.TAKE_PROFIT)) {
                takeProfitTextField.setValue(priceTriggerViewModel.getTriggerPrice());
            }
        });
    }
}
