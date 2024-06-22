package org.dharbar.telegabot.repository.config;

import org.dharbar.telegabot.repository.entity.OrderEntity;
import org.dharbar.telegabot.repository.entity.TradeEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback;

import java.util.UUID;

@Configuration
public class RepositoryUUIDConfig {

    @Bean
    BeforeConvertCallback<OrderEntity> beforeConvertOrderEntityCallback() {
        return (order) -> {
            if (order.getId() == null) {
                order.setId(UUID.randomUUID());
            }
            return order;
        };
    }

    @Bean
    BeforeConvertCallback<TradeEntity> beforeConvertTradeEntityCallback() {
        return (trade) -> {
            if (trade.getId() == null) {
                trade.setId(UUID.randomUUID());
            }
            return trade;
        };
    }
}
