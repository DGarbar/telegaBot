package org.dharbar.telegabot.repository;

import org.dharbar.telegabot.repository.entity.PositionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PositionRepository extends PagingAndSortingRepository<PositionEntity, UUID>, CrudRepository<PositionEntity, UUID> {

    @EntityGraph(attributePaths = {"orders"})
    Page<PositionEntity> findAll(Specification<PositionEntity> spec, Pageable pageRequest);
}
