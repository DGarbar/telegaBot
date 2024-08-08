package org.dharbar.telegabot.controller.request;

import lombok.Builder;
import lombok.Data;
import org.dharbar.telegabot.repository.entity.OrderType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class CreateOrderRequest {

    OrderType type;
    String ticker;
    BigDecimal quantity;
    BigDecimal rate;
    LocalDate dateAt;
    BigDecimal totalUsd;
    BigDecimal totalUah;
    BigDecimal commissionUsd;
    String comment;
}
