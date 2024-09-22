package org.dharbar.telegabot.repository;

import org.dharbar.telegabot.repository.entity.TickerEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TickerRepository extends CrudRepository<TickerEntity, String> {

    List<TickerEntity> findAll();

}
