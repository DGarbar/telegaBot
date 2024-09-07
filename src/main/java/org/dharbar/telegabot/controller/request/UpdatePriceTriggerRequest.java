package org.dharbar.telegabot.controller.request;

import lombok.Builder;
import lombok.Data;
import org.dharbar.telegabot.repository.entity.TriggerType;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class UpdatePriceTriggerRequest {
    UUID id;
    TriggerType type;
    BigDecimal triggerPrice;
    Boolean isEnabled;
}
