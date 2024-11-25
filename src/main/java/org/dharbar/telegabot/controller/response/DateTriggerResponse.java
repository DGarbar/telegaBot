package org.dharbar.telegabot.controller.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DateTriggerResponse {

    UUID id;
    LocalDateTime triggerDate;
    String comment;
}
