package org.dharbar.telegabot.controller.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class CreatePositionRequest {

    String ticker;

    UUID portfolioId;

    List<CreateOrderRequest> orders = new ArrayList<>();

    String comment;
}
