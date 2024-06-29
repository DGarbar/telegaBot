package org.dharbar.telegabot.repository;

import org.dharbar.telegabot.repository.entity.StockPriceEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockPriceRepository extends CrudRepository<StockPriceEntity, String> {

    List<StockPriceEntity> findAll();

}
