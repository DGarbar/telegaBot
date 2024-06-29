package org.dharbar.telegabot.repository;

import org.dharbar.telegabot.repository.entity.StockPriceEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockPriceRepository extends CrudRepository<StockPriceEntity, String> {

}
