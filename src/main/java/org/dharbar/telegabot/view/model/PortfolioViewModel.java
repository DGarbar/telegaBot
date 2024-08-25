package org.dharbar.telegabot.view.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PortfolioViewModel {

    UUID id;
    String name;
    String description;

}
