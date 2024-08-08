package org.dharbar.telegabot.repository;

import org.dharbar.telegabot.repository.entity.PositionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PositionRepository extends PagingAndSortingRepository<PositionEntity, UUID>, CrudRepository<PositionEntity, UUID> {

    Page<PositionEntity> findAllByIsClosedIsFalse(PageRequest pageRequest);
}
