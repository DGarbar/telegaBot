package org.dharbar.telegabot.repository;

import org.dharbar.telegabot.repository.entity.AlarmEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AlarmRepository extends CrudRepository<AlarmEntity, UUID> {

    void deleteByPositionIdAndId(UUID positionId, UUID id);
}
