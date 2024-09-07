package org.dharbar.telegabot.service.positionmanagment.dto;

import lombok.Builder;
import lombok.Data;
import org.dharbar.telegabot.repository.entity.TriggerType;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class PriceTriggerDto {
    UUID id;
    TriggerType type;
    BigDecimal triggerPrice;
    Boolean isEnabled;
}
