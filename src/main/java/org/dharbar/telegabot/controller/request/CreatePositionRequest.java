package org.dharbar.telegabot.controller.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreatePositionRequest {

    String ticker;

    List<CreateOrderRequest> orders = new ArrayList<>();
}
