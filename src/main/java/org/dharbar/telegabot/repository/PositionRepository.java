package org.dharbar.telegabot.repository;

import feign.Param;
import jakarta.persistence.LockModeType;
import org.dharbar.telegabot.repository.entity.PositionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PositionRepository extends PagingAndSortingRepository<PositionEntity, UUID>, CrudRepository<PositionEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PositionEntity p WHERE p.id = :id")
    Optional<PositionEntity> findByIdForUpdate(@Param("id") UUID id);

    @EntityGraph(attributePaths = {"orders", "priceTriggers"})
    Page<PositionEntity> findAll(Specification<PositionEntity> spec, Pageable pageRequest);
}
