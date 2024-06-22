package org.dharbar.telegabot.job.jobs;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.log4j.Log4j2;
import org.dharbar.telegabot.bot.TelegramBot;
import org.dharbar.telegabot.service.rate.RateFacadeService;
import org.dharbar.telegabot.service.rate.dto.RateDto;
import org.dharbar.telegabot.service.rate.dto.RateProvider;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Log4j2
@RequiredArgsConstructor
public class AlertPriceJob extends BotJob {

    public static final String TARGET_PRICE_PARAMETER = "targetPrice";

    private final RateFacadeService rateService;
    private final TelegramBot telegramBot;

    @Override
    public void execute(JobExecutionContext context) {
        AlertPriceJobData data = AlertPriceJobData.from(context.getMergedJobDataMap());

        Map<RateProvider, List<RateDto>> cryptoRates = rateService.getCryptoRates();
        cryptoRates.get(RateProvider.BINANCE).stream()
                .filter(rateDto -> rateDto.getRateSell() <= data.targetPrice)
                .findFirst()
                .ifPresent(rateDto -> sendMessage(rateDto, data));
    }

    private void sendMessage(RateDto rateDto, AlertPriceJobData data) {
        String message = String.format("Can buy USDT = %.2f", rateDto.getRateSell());
        telegramBot.sendMessage(data.chatId, message);
    }

    @Value
    @Builder
    public static class AlertPriceJobData {
        long chatId;
        double targetPrice;

        public static AlertPriceJobData from(JobDataMap jobDataMap) {
            return new AlertPriceJobData(
                    jobDataMap.getLongValue(CHAT_ID_PARAMETER),
                    jobDataMap.getDouble(TARGET_PRICE_PARAMETER));
        }

        public JobDataMap toDataMap() {
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put(CHAT_ID_PARAMETER, getChatId());
            jobDataMap.put(TARGET_PRICE_PARAMETER, getTargetPrice());
            return jobDataMap;
        }
    }
}

