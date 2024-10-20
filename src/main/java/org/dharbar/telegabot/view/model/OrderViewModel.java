package org.dharbar.telegabot.view.model;

import lombok.Builder;
import lombok.Data;
import org.dharbar.telegabot.repository.entity.OrderType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class OrderViewModel {

    UUID id;

    UUID positionId;

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
