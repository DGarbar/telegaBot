package org.dharbar.telegabot.service.pricetrigger;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.service.pricetrigger.strategy.TriggerStrategy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceTriggerService {

    // // private final AlarmService alarmService;
    private final List<TriggerStrategy> triggerStrategies;

    public void checkEndOfDay(String ticker, BigDecimal low, BigDecimal high, LocalDateTime time) {
        triggerStrategies.forEach(strategy -> strategy.checkEndOfDay(ticker, low, high, time));
    }

}
