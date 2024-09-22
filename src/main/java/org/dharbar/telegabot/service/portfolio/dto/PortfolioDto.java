package org.dharbar.telegabot.service.portfolio.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PortfolioDto {

    UUID id;

    String name;
    String description;

    // PortfolioType type;
}
