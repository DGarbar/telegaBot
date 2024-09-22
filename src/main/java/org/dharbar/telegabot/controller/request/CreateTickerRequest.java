package org.dharbar.telegabot.controller.request;

import lombok.Builder;
import lombok.Data;
import org.dharbar.telegabot.repository.entity.TickerType;

@Data
@Builder
public class CreateTickerRequest {

    String ticker;
    TickerType type;
}
