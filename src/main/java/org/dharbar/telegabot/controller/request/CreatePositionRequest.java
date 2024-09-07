package org.dharbar.telegabot.controller.request;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
public class CreatePositionRequest {

    String ticker;

    UUID portfolioId;

    Set<CreateOrderRequest> orders = new HashSet<>();

    Set<CreatePriceTriggerRequest> priceTriggers= new HashSet<>();

    String comment;
}
