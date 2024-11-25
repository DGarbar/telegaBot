package org.dharbar.telegabot.service.trigger.price.strategy;

import org.dharbar.telegabot.bot.TelegramBot;
import org.dharbar.telegabot.repository.PriceTriggerRepository;
import org.dharbar.telegabot.repository.entity.PriceTriggerEntity;
import org.dharbar.telegabot.repository.entity.TriggerType;
import org.dharbar.telegabot.service.alarm.AlarmService;
import org.dharbar.telegabot.service.ticker.dto.TickerPrice;
import org.dharbar.telegabot.service.ticker.dto.TickerRangePrice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class StopLossTriggerStrategy extends TriggerStrategy {

    protected StopLossTriggerStrategy(AlarmService alarmService, PriceTriggerRepository priceTriggerRepository, TelegramBot telegramBot) {
        super(alarmService, priceTriggerRepository, telegramBot);
    }

    @Override
    @Transactional
    public void checkEndOfDay(TickerRangePrice tickerRangePrice) {
        String ticker = tickerRangePrice.getTicker();
        BigDecimal low = tickerRangePrice.getLowPrice();
        List<PriceTriggerEntity> stopLossTriggers = priceTriggerRepository.findAllByTickerAndTypeAndPriceMore(ticker, type(), low);
        process(stopLossTriggers);
    }

    @Override
    public void checkCurrent(TickerPrice latestPrice) {
        String ticker = latestPrice.getTicker();
        BigDecimal price = latestPrice.getPrice();
        List<PriceTriggerEntity> stopLossTriggers = priceTriggerRepository.findAllByTickerAndTypeAndPriceMore(ticker, type(), price);
        process(stopLossTriggers);
    }

    private void process(List<PriceTriggerEntity> stopLossTriggers) {
        for (PriceTriggerEntity stopLossTrigger : stopLossTriggers) {

            // !!! Why do I need alarm if I can just see IsTriggered
            alarmService.createAlarm(stopLossTrigger.getPosition().getId(), type());
            stopLossTrigger.setIsTriggered(true);
            priceTriggerRepository.save(stopLossTrigger);

            sendMessage("Stop loss triggered for " + stopLossTrigger.getPosition().getTicker() + " at " + stopLossTrigger.getTriggerPrice());
        }
    }

    @Override
    public TriggerType type() {
        return TriggerType.STOP_LOSS;
    }

}
