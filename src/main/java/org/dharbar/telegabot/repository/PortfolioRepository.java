package org.dharbar.telegabot.repository;

import org.dharbar.telegabot.repository.entity.PortfolioEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PortfolioRepository extends CrudRepository<PortfolioEntity, UUID> {

}
