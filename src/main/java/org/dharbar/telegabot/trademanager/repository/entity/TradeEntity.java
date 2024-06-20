package org.dharbar.telegabot.trademanager.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Table("trade")
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class TradeEntity {

    @Id
    private UUID id;

    @Builder.Default
    @MappedCollection(idColumn = "trade_id")
    private Set<OrderEntity> orders = new HashSet<>();

    private Boolean isClosed;

    // sell - buy - commission
    private BigDecimal netProfitUsd;
    private BigDecimal profitPercentage;
    private String comment;

    public void addOrder(OrderEntity order) {
        order.setTradeId(this.id);
        orders.add(order);
    }
}
