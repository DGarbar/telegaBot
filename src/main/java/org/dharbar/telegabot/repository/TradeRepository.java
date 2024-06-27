package org.dharbar.telegabot.repository;

import org.dharbar.telegabot.repository.entity.TradeEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface TradeRepository extends PagingAndSortingRepository<TradeEntity, UUID>, CrudRepository<TradeEntity, UUID> {

    List<TradeEntity> findAllByIsClosedIsFalse(PageRequest pageRequest);

    @Query("SELECT DISTINCT ticker FROM \"trade\"")
    Set<String> findDistinctTicker();
}
