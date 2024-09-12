package org.dharbar.telegabot.controller.response;

import lombok.Builder;
import lombok.Data;
import org.dharbar.telegabot.repository.entity.TriggerType;

import java.util.UUID;

@Data
@Builder
public class AlarmResponse {

    UUID id;

    TriggerType type;
}
