package org.dharbar.telegabot.controller.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UpdateDateTriggerRequest {
    UUID id;
    LocalDateTime triggerDate;
    String comment;
}
