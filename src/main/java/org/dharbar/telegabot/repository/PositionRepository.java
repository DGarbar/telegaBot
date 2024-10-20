package org.dharbar.telegabot.repository;

import feign.Param;
import jakarta.persistence.LockModeType;
import org.dharbar.telegabot.repository.entity.PositionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PositionRepository extends PagingAndSortingRepository<PositionEntity, UUID>, CrudRepository<PositionEntity, UUID>, JpaRepository<PositionEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PositionEntity p WHERE p.id = :id")
    Optional<PositionEntity> findByIdForUpdate(@Param("id") UUID id);

    @EntityGraph(attributePaths = {"orders", "priceTriggers", "alarms"})
    Page<PositionEntity> findAll(Specification<PositionEntity> spec, Pageable pageRequest);

    @EntityGraph(attributePaths = {"orders", "priceTriggers", "alarms"})
    Optional<PositionEntity> findById(UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o.position FROM OrderEntity o WHERE o.id = :orderId")
    Optional<PositionEntity> findByOrderIdForUpdate(UUID orderId);
}
