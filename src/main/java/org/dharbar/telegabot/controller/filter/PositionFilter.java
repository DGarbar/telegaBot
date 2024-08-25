package org.dharbar.telegabot.controller.filter;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PositionFilter {

    String ticker;
    Boolean isClosed;
    UUID portfolioId;
}
