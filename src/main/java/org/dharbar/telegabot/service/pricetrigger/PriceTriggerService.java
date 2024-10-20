package org.dharbar.telegabot.service.pricetrigger;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.service.pricetrigger.strategy.TriggerStrategy;
import org.dharbar.telegabot.service.ticker.dto.TickerPrice;
import org.dharbar.telegabot.service.ticker.dto.TickerRangePrice;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceTriggerService {

    // // private final AlarmService alarmService;
    private final List<TriggerStrategy> triggerStrategies;

    public void checkEndOfDay(TickerRangePrice tickerDayPrice) {
        triggerStrategies.forEach(strategy -> strategy.checkEndOfDay(tickerDayPrice));
    }

    public void checkCurrent(TickerPrice latestPrice) {
        triggerStrategies.forEach(strategy -> strategy.checkCurrent(latestPrice));
    }
}
