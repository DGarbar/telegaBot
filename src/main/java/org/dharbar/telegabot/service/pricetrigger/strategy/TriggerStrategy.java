package org.dharbar.telegabot.service.pricetrigger.strategy;

import org.dharbar.telegabot.repository.PriceTriggerRepository;
import org.dharbar.telegabot.repository.entity.TriggerType;
import org.dharbar.telegabot.service.alarm.AlarmService;
import org.dharbar.telegabot.service.ticker.dto.TickerPrice;
import org.dharbar.telegabot.service.ticker.dto.TickerRangePrice;

public abstract class TriggerStrategy {

    protected final AlarmService alarmService;
    protected final PriceTriggerRepository priceTriggerRepository;

    protected TriggerStrategy(AlarmService alarmService, PriceTriggerRepository priceTriggerRepository) {
        this.alarmService = alarmService;
        this.priceTriggerRepository = priceTriggerRepository;
    }

    public abstract void checkEndOfDay(TickerRangePrice tickerRangePrice);

    public abstract void checkCurrent(TickerPrice latestPrice);

    public abstract TriggerType type();

    public boolean isSupports(TriggerType type) {
        return type().equals(type);
    }
}
