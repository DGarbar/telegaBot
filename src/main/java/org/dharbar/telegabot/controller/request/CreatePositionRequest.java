package org.dharbar.telegabot.controller.request;

import lombok.Data;
import org.dharbar.telegabot.repository.entity.PositionType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
public class CreatePositionRequest {

    String ticker;

    PositionType type;

    UUID portfolioId;

    Set<CreateOrderRequest> orders = new HashSet<>();

    Set<CreatePriceTriggerRequest> priceTriggers= new HashSet<>();

    String comment;
}
