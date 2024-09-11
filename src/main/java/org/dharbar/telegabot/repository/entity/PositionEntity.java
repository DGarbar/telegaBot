package org.dharbar.telegabot.repository.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "position")
@Entity
public class PositionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String ticker;

    private UUID portfolioId;

    @Setter(AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "position", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderEntity> orders = new HashSet<>();

    @Setter(AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "position", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PriceTriggerEntity> priceTriggers = new HashSet<>();

    @Setter(AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "position", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AlarmEntity> alarms = new HashSet<>();

    @Column(nullable = false)
    private LocalDate openAt;
    private LocalDate closedAt;

    @Column(nullable = false)
    private Boolean isClosed;

    @Column(nullable = false)
    private BigDecimal buyTotalAmount;
    @Column(nullable = false)
    private BigDecimal buyQuantity;
    @Column(nullable = false)
    private BigDecimal buyAveragePrice;

    @Column(nullable = false)
    private BigDecimal sellTotalAmount;
    @Column(nullable = false)
    private BigDecimal sellQuantity;
    @Column(nullable = false)
    private BigDecimal sellAveragePrice;

    @Column(nullable = false)
    private BigDecimal commissionTotalAmount;

    // sell - buy - commission
    @Column(nullable = false)
    private BigDecimal netProfitAmount;
    @Column(nullable = false)
    private BigDecimal profitPercentage;

    private String comment;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public void addOrder(OrderEntity order) {
        orders.add(order);
        order.setPosition(this);
    }

    public void removeOrder(OrderEntity order) {
        orders.remove(order);
        order.setPosition(null);
    }

    public void addPriceTrigger(PriceTriggerEntity priceTrigger) {
        priceTriggers.add(priceTrigger);
        priceTrigger.setPosition(this);
    }

    public void removePriceTrigger(PriceTriggerEntity priceTrigger) {
        priceTriggers.remove(priceTrigger);
        priceTrigger.setPosition(null);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ?
                ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() :
                o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ?
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() :
                this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        PositionEntity that = (PositionEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ?
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() :
                getClass().hashCode();
    }
}
