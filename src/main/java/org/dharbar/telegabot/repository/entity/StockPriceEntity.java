package org.dharbar.telegabot.repository.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@Table("stock_price")
public class StockPriceEntity implements Persistable<String> {
    @Id
    private String ticker;
    private BigDecimal price;
    private LocalDateTime updatedAt;

    @Transient
    private boolean newEntity;

    @Override
    public String getId() {
        return getTicker();
    }

    @Override
    @JsonIgnore
    public boolean isNew() {
        return newEntity;
    }
}
