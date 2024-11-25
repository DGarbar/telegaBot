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
public class TakeProfitTriggerStrategy extends TriggerStrategy {

    protected TakeProfitTriggerStrategy(AlarmService alarmService, PriceTriggerRepository priceTriggerRepository, TelegramBot telegramBot) {
        super(alarmService, priceTriggerRepository, telegramBot);
    }

    @Override
    @Transactional
    public void checkEndOfDay(TickerRangePrice tickerRangePrice) {
        String ticker = tickerRangePrice.getTicker();
        BigDecimal high = tickerRangePrice.getHighPrice();
        List<PriceTriggerEntity> stopLossTriggers = priceTriggerRepository.findAllByTickerAndTypeAndPriceLess(ticker, type(), high);
        process(stopLossTriggers);
    }

    @Override
    public void checkCurrent(TickerPrice latestPrice) {
        String ticker = latestPrice.getTicker();
        BigDecimal price = latestPrice.getPrice();
        List<PriceTriggerEntity> stopLossTriggers = priceTriggerRepository.findAllByTickerAndTypeAndPriceLess(ticker, type(), price);
        process(stopLossTriggers);
    }

    private void process(List<PriceTriggerEntity> stopLossTriggers) {
        for (PriceTriggerEntity stopLossTrigger : stopLossTriggers) {
            alarmService.createAlarm(stopLossTrigger.getPosition().getId(), type());
            stopLossTrigger.setIsTriggered(true);
            priceTriggerRepository.save(stopLossTrigger);

            sendMessage("Take profit triggered for " + stopLossTrigger.getPosition().getTicker() + " at " + stopLossTrigger.getTriggerPrice());
        }
    }

    @Override
    public TriggerType type() {
        return TriggerType.TAKE_PROFIT;
    }

}
