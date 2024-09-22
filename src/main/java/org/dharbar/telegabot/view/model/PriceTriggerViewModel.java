package org.dharbar.telegabot.view.model;

import lombok.Builder;
import lombok.Data;
import org.dharbar.telegabot.repository.entity.TriggerType;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class PriceTriggerViewModel {
    UUID id;
    TriggerType type;
    BigDecimal triggerPrice;
    Boolean isTriggered;
    Boolean isEnabled;
}
