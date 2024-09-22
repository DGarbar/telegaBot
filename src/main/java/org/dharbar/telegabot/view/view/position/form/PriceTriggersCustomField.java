package org.dharbar.telegabot.view.view.position.form;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import org.dharbar.telegabot.repository.entity.TriggerType;
import org.dharbar.telegabot.view.model.PriceTriggerViewModel;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

public class PriceTriggersCustomField extends CustomField<Set<PriceTriggerViewModel>> {

    private final BigDecimalField stopLossField = new BigDecimalField("Stop Loss");
    private final Checkbox stopLossEnabledCheckbox = new Checkbox("E s", true);

    private final BigDecimalField takeProfitField = new BigDecimalField("Take Profit");
    private final Checkbox takeProfitEnabledCheckbox = new Checkbox("E t", true);

    private final BigDecimalField gridBuyPriceField = new BigDecimalField("Grid Buy Price");
    private final Checkbox gridBuyEnabledCheckbox = new Checkbox("E Gb", true);
    private final HorizontalLayout gridBuyLayout = new HorizontalLayout();

    private final BigDecimalField gridSellPriceField = new BigDecimalField("Grid Sell Price");
    private final Checkbox gridSellEnabledCheckbox = new Checkbox("E gs", true);
    private final HorizontalLayout gridSellLayout = new HorizontalLayout();

    public PriceTriggersCustomField() {
        setLabel("Price Settings");
        setup();
        showGridSettings(false);
    }

    private void setup() {
        stopLossField.setSuffixComponent(VaadinIcon.DOLLAR.create());
        stopLossField.setClearButtonVisible(true);
        HorizontalLayout stopLossLayout = new HorizontalLayout(stopLossField, stopLossEnabledCheckbox);

        takeProfitField.setSuffixComponent(VaadinIcon.DOLLAR.create());
        takeProfitField.setClearButtonVisible(true);
        HorizontalLayout takeProfitLayout = new HorizontalLayout(takeProfitField, takeProfitEnabledCheckbox);

        gridBuyPriceField.setSuffixComponent(VaadinIcon.DOLLAR.create());
        gridBuyPriceField.setClearButtonVisible(true);
        gridBuyLayout.add(gridBuyPriceField, gridBuyEnabledCheckbox);

        gridSellPriceField.setSuffixComponent(VaadinIcon.DOLLAR.create());
        gridSellPriceField.setClearButtonVisible(true);
        gridSellLayout.add(gridSellPriceField, gridSellEnabledCheckbox);

        VerticalLayout verticalLayout = new VerticalLayout(stopLossLayout, takeProfitLayout, gridBuyLayout, gridSellLayout);
        add(verticalLayout);
    }

    public void showGridSettings(boolean isShow) {
        gridBuyLayout.setVisible(isShow);
        gridSellLayout.setVisible(isShow);
    }

    private void updatePriceTrigger(TriggerType type, BigDecimal value, Boolean isEnabled) {
        if (value != null && !value.equals(BigDecimal.ZERO)) {
            addPositionPriceTrigger(type, isEnabled, value);
        } else {
            removePositionPriceTrigger(type);
        }
    }

    private void addPositionPriceTrigger(TriggerType triggerType, Boolean isEnabled, BigDecimal triggerPrice) {
        Set<PriceTriggerViewModel> priceTriggers = getValue();

        Optional<PriceTriggerViewModel> trigger = priceTriggers.stream()
                .filter(priceTrigger -> priceTrigger.getType().equals(triggerType))
                .findFirst();

        if (trigger.isPresent()) {
            PriceTriggerViewModel priceTriggerViewModel = trigger.get();
            priceTriggerViewModel.setTriggerPrice(triggerPrice);
            priceTriggerViewModel.setIsEnabled(isEnabled);
        } else {
            priceTriggers.add(PriceTriggerViewModel.builder()
                    .type(triggerType)
                    .triggerPrice(triggerPrice)
                    .isEnabled(isEnabled)
                    .build());
        }
    }

    private void removePositionPriceTrigger(TriggerType triggerType) {
        Set<PriceTriggerViewModel> priceTriggers = getValue();
        priceTriggers.removeIf(priceTrigger -> priceTrigger.getType().equals(triggerType));
    }

    // TODO Why I updating real object. I will loose not saved progress ufter save
    @Override
    protected Set<PriceTriggerViewModel> generateModelValue() {
        // It will be triggered on ANY change
        updatePriceTrigger(TriggerType.STOP_LOSS, stopLossField.getValue(), stopLossEnabledCheckbox.getValue());
        updatePriceTrigger(TriggerType.TAKE_PROFIT, takeProfitField.getValue(), takeProfitEnabledCheckbox.getValue());
        updatePriceTrigger(TriggerType.GRID_BUY, gridBuyPriceField.getValue(), gridBuyEnabledCheckbox.getValue());
        updatePriceTrigger(TriggerType.GRID_SELL, gridSellPriceField.getValue(), gridSellEnabledCheckbox.getValue());
        return getValue();
    }

    @Override
    protected void setPresentationValue(Set<PriceTriggerViewModel> priceTriggerViewModels) {
        stopLossField.clear();
        stopLossEnabledCheckbox.setValue(true);
        takeProfitField.clear();
        takeProfitEnabledCheckbox.setValue(true);
        gridBuyPriceField.clear();
        gridSellEnabledCheckbox.setValue(true);
        gridSellPriceField.clear();
        gridSellEnabledCheckbox.setValue(true);

        priceTriggerViewModels.forEach(priceTriggerViewModel -> {
            TriggerType type = priceTriggerViewModel.getType();
            if (type.equals(TriggerType.STOP_LOSS)) {
                stopLossField.setValue(priceTriggerViewModel.getTriggerPrice());
                stopLossEnabledCheckbox.setValue(priceTriggerViewModel.getIsEnabled());
            } else if (type.equals(TriggerType.TAKE_PROFIT)) {
                takeProfitField.setValue(priceTriggerViewModel.getTriggerPrice());
                takeProfitEnabledCheckbox.setValue(priceTriggerViewModel.getIsEnabled());
            } else if (type.equals(TriggerType.GRID_BUY)) {
                gridBuyPriceField.setValue(priceTriggerViewModel.getTriggerPrice());
                gridBuyEnabledCheckbox.setValue(priceTriggerViewModel.getIsEnabled());
            } else if (type.equals(TriggerType.GRID_SELL)) {
                gridSellPriceField.setValue(priceTriggerViewModel.getTriggerPrice());
                gridSellEnabledCheckbox.setValue(priceTriggerViewModel.getIsEnabled());
            }
        });
    }
}
