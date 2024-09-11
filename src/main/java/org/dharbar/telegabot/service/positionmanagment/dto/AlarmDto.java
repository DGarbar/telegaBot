package org.dharbar.telegabot.service.positionmanagment.dto;

import lombok.Builder;
import lombok.Data;
import org.dharbar.telegabot.repository.entity.AlarmType;

import java.util.UUID;

@Data
@Builder
public class AlarmDto {

    UUID id;

    AlarmType type;
}
