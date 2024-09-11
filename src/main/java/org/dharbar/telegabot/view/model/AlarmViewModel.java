package org.dharbar.telegabot.view.model;

import org.dharbar.telegabot.repository.entity.TriggerType;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlarmViewModel {
    UUID id;
    TriggerType type;

    UUID positionId;
}
