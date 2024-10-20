package org.dharbar.telegabot.repository;

import org.dharbar.telegabot.repository.entity.PriceTriggerEntity;
import org.dharbar.telegabot.repository.entity.TriggerType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface PriceTriggerRepository extends CrudRepository<PriceTriggerEntity, UUID> {

    @Query("SELECT p FROM PriceTriggerEntity p "
            + "WHERE p.position.ticker = :ticker "
            + "and p.triggerPrice BETWEEN :low and :high "
            + "and p.type = :type "
            + "and p.isTriggered = false")
    List<PriceTriggerEntity> findAllByTickerAndTypeAndPriceIn(String ticker,
                                                              TriggerType type,
                                                              BigDecimal low,
                                                              BigDecimal high);

    // Stop Loss
    @Query("SELECT p FROM PriceTriggerEntity p "
            + "WHERE p.position.ticker = :ticker "
            + "and p.triggerPrice >= :price "
            + "and p.type = :type "
            + "and p.isTriggered = false")
    List<PriceTriggerEntity> findAllByTickerAndTypeAndPriceMore(String ticker,
                                                                TriggerType type,
                                                                BigDecimal price);

    // Take Profit
    @Query("SELECT p FROM PriceTriggerEntity p "
            + "WHERE p.position.ticker = :ticker "
            + "and p.triggerPrice <= :price "
            + "and p.type = :type "
            + "and p.isTriggered = false")
    List<PriceTriggerEntity> findAllByTickerAndTypeAndPriceLess(String ticker,
                                                                TriggerType type,
                                                                BigDecimal price);
}
