package org.dharbar.telegabot.service.position.dto;

import lombok.Builder;
import lombok.Data;
import org.dharbar.telegabot.repository.entity.TriggerType;

import java.util.UUID;

@Data
@Builder
public class AlarmDto {

    UUID id;

    TriggerType type;
}
