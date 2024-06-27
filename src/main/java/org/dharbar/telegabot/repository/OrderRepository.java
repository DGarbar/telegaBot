package org.dharbar.telegabot.repository;

import org.dharbar.telegabot.repository.entity.OrderEntity;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface OrderRepository extends CrudRepository<OrderEntity, UUID> {

    @Query("SELECT DISTINCT ticker FROM \"order\"")
    Set<String> findDistinctTicker();
}
