package org.dharbar.telegabot.controller.response;

import org.dharbar.telegabot.repository.entity.AlarmType;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlarmResponse {

    UUID id;

    AlarmType type;
}
