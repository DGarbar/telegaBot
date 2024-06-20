package org.dharbar.telegabot.trademanager.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Table("order")
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntity {
    @Id
    private UUID id;

    private UUID tradeId;

    private OrderType type;
    private String ticker;
    private Double quantity;
    private BigDecimal rate;
    private LocalDate dateAt;
    private BigDecimal totalUsd;
    private BigDecimal totalUah;
    private BigDecimal commissionUsd;

    private String comment;
}
