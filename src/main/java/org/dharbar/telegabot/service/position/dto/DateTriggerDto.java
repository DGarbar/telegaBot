package org.dharbar.telegabot.service.position.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DateTriggerDto {
    UUID id;
    LocalDateTime triggerDate;
    String comment;

}
