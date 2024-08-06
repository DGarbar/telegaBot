package org.dharbar.telegabot.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@Table("order")
public class OrderEntity {
    @Id
    private UUID id;

    private UUID positionId;

    private OrderType type;
    private String ticker;
    private BigDecimal quantity;
    private BigDecimal rate;
    private LocalDate dateAt;
    private BigDecimal totalUsd;
    private BigDecimal totalUah;
    private BigDecimal commissionUsd;

    private String comment;
}
