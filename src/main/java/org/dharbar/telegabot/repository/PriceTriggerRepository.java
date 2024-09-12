package org.dharbar.telegabot.repository;

import org.dharbar.telegabot.repository.entity.PriceTriggerEntity;
import org.dharbar.telegabot.repository.entity.TriggerType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PriceTriggerRepository extends CrudRepository<PriceTriggerEntity, UUID> {

    @Query("SELECT p FROM PriceTriggerEntity p WHERE p.position.ticker = :ticker and p.type = :type")
    List<PriceTriggerEntity> findAllByTickerAndType(String ticker, TriggerType type);
}
