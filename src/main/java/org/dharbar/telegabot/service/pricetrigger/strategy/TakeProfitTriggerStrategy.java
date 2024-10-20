package org.dharbar.telegabot.service.pricetrigger.strategy;

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

    protected TakeProfitTriggerStrategy(AlarmService alarmService, PriceTriggerRepository priceTriggerRepository) {
        super(alarmService, priceTriggerRepository);
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
        }
    }

    @Override
    public TriggerType type() {
        return TriggerType.TAKE_PROFIT;
    }

}
