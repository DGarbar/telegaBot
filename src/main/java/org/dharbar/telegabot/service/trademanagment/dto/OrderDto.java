package org.dharbar.telegabot.service.trademanagment.dto;

import lombok.Builder;
import lombok.Value;
import org.dharbar.telegabot.repository.entity.OrderType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Value
@Builder
public class OrderDto {

    UUID id;

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
