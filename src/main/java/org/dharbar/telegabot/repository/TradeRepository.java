package org.dharbar.telegabot.repository;

import org.dharbar.telegabot.repository.entity.TradeEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TradeRepository extends PagingAndSortingRepository<TradeEntity, UUID>, CrudRepository<TradeEntity, UUID> {

    List<TradeEntity> findAllByIsClosedIsFalse(PageRequest pageRequest);
}
