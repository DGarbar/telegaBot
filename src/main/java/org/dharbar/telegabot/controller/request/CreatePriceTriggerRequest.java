package org.dharbar.telegabot.controller.request;

import lombok.Builder;
import lombok.Data;
import org.dharbar.telegabot.repository.entity.TriggerType;

import java.math.BigDecimal;

@Data
@Builder
public class CreatePriceTriggerRequest {
    TriggerType type;
    BigDecimal triggerPrice;
    Boolean isEnabled;
}
