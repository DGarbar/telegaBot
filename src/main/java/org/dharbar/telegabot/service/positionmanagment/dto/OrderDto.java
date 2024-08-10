package org.dharbar.telegabot.service.positionmanagment.dto;

import lombok.Builder;
import lombok.Data;
import org.dharbar.telegabot.repository.entity.OrderType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
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
    BigDecimal commissionAmount;

    String comment;
}
