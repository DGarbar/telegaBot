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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@Table("trade")
public class TradeEntity {

    @Id
    private UUID id;

    @MappedCollection(idColumn = "trade_id")
    private Set<OrderEntity> orders = new HashSet<>();

    private Boolean isClosed;

    // sell - buy - commission
    private BigDecimal netProfitUsd;
    private BigDecimal profitPercentage;
    private String comment;

    public void addOrder(OrderEntity order) {
        order.setId(UUID.randomUUID());
        order.setTradeId(this.id);
        orders.add(order);
    }
}
