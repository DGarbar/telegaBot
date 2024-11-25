package org.dharbar.telegabot.service.trigger.price.strategy;

import org.dharbar.telegabot.bot.TelegramBot;
import org.dharbar.telegabot.repository.PriceTriggerRepository;
import org.dharbar.telegabot.repository.entity.TriggerType;
import org.dharbar.telegabot.service.alarm.AlarmService;
import org.dharbar.telegabot.service.ticker.dto.TickerPrice;
import org.dharbar.telegabot.service.ticker.dto.TickerRangePrice;
import org.springframework.beans.factory.annotation.Value;

public abstract class TriggerStrategy {

    @Value("${telegram.my.chat-id}")
    Long myChatId;

    protected final AlarmService alarmService;
    protected final PriceTriggerRepository priceTriggerRepository;

    // TODO extract into event ?
    protected final TelegramBot telegramBot;

    protected TriggerStrategy(AlarmService alarmService, PriceTriggerRepository priceTriggerRepository, TelegramBot telegramBot) {
        this.alarmService = alarmService;
        this.priceTriggerRepository = priceTriggerRepository;
        this.telegramBot = telegramBot;
    }

    public abstract void checkEndOfDay(TickerRangePrice tickerRangePrice);

    public abstract void checkCurrent(TickerPrice latestPrice);

    public abstract TriggerType type();

    public boolean isSupports(TriggerType type) {
        return type().equals(type);
    }

    protected void sendMessage(String message) {
        telegramBot.sendMessage(myChatId, message);
    }
}
