package org.dharbar.telegabot.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@Table("position")
public class PositionEntity {

    @Id
    private UUID id;

    private String ticker;

    private LocalDate dateAt;

    @MappedCollection(idColumn = "position_id")
    private Set<OrderEntity> orders = new HashSet<>();

    private Boolean isClosed;

    // sell - buy - commission
    private BigDecimal netProfitUsd;
    private BigDecimal profitPercentage;
    private String comment;

    public void addOrder(OrderEntity order) {
        order.setPositionId(this.id);
        orders.add(order);
    }
}
