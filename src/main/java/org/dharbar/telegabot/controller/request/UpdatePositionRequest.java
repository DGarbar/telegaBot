package org.dharbar.telegabot.controller.request;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
public class UpdatePositionRequest {

    String ticker;

    UUID portfolioId;

    Set<UpdateOrderRequest> orders = new HashSet<>();
    Set<UpdatePriceTriggerRequest> priceTriggers = new HashSet<>();

    String comment;
}
