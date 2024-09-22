package org.dharbar.telegabot.service.pricetrigger.strategy;

import org.dharbar.telegabot.repository.PriceTriggerRepository;
import org.dharbar.telegabot.repository.entity.PriceTriggerEntity;
import org.dharbar.telegabot.repository.entity.TriggerType;
import org.dharbar.telegabot.service.alarm.AlarmService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StopLossTriggerStrategy extends TriggerStrategy {

    protected StopLossTriggerStrategy(AlarmService alarmService, PriceTriggerRepository priceTriggerRepository) {
        super(alarmService, priceTriggerRepository);
    }

    @Override
    @Transactional
    public void checkEndOfDay(String ticker, BigDecimal low, BigDecimal high, LocalDateTime date) {
        List<PriceTriggerEntity> stopLossTriggers = priceTriggerRepository.findAllByTickerAndTypeAndPriceIn(ticker, TriggerType.STOP_LOSS, low, high);
        for (PriceTriggerEntity stopLossTrigger : stopLossTriggers) {
            alarmService.createAlarm(stopLossTrigger.getPosition().getId(), TriggerType.STOP_LOSS);
            stopLossTrigger.setIsTriggered(true);
            priceTriggerRepository.save(stopLossTrigger);
        }
    }

    @Override
    public TriggerType type() {
        return TriggerType.STOP_LOSS;
    }

}
