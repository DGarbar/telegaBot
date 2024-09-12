package org.dharbar.telegabot.service.pricetrigger;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PriceTriggerService {

    // // private final AlarmService alarmService;
    //
    // private final PriceTriggerRepository priceTriggerRepository;
    //
    //
    // @Transactional
    // public void checkAndCreteAlarms(String ticker, BigDecimal price) {
    //
    //
    //     // Find triggers by ticker
    //     // Check for trigger strategy by price
    //     // If strategy is met, add alarm
    //     // Add alarm only if not duplicated already
    //
    // }
    //
    // private void processStopLoss(String ticker, BigDecimal price) {
    //     List<PriceTriggerEntity> stopLossTriggers = priceTriggerRepository.findAllByTickerAndType(ticker, TriggerType.STOP_LOSS);
    //     for (PriceTriggerEntity stopLossTrigger : stopLossTriggers) {
    //         BigDecimal stopLossPrice = stopLossTrigger.getTriggerPrice();
    //         if (price.compareTo(stopLossPrice) <= 0) {
    //             alarmService.createAlarm(stopLossTrigger.getPosition().getId(), TriggerType.STOP_LOSS);
    //             // TODO add alarm triggering
    //         }
    //     }
    //
    // }
    //
    // // Method for dayHigh and dayLow

}
