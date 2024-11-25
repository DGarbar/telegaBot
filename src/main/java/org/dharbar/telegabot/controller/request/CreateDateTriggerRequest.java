package org.dharbar.telegabot.controller.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CreateDateTriggerRequest {
    LocalDateTime triggerDate;
    String comment;
}
