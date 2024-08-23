package org.dharbar.telegabot.controller.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PortfolioResponse {

    UUID id;

    String name;
    String description;
}
