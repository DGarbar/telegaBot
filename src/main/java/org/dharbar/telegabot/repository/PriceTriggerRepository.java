package org.dharbar.telegabot.repository;

import org.dharbar.telegabot.repository.entity.PriceTriggerEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PriceTriggerRepository extends CrudRepository<PriceTriggerEntity, UUID> {

}
