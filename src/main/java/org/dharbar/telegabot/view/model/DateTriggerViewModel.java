package org.dharbar.telegabot.view.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DateTriggerViewModel {
    UUID id;
    LocalDateTime triggerDate;
    String comment;
}
