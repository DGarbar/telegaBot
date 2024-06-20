package org.dharbar.telegabot.trademanager.repository;

import org.dharbar.telegabot.trademanager.repository.entity.TradeEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TradeRepository extends CrudRepository<TradeEntity, UUID> {
}
