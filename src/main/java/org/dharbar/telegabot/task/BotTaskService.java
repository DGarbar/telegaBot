package org.dharbar.telegabot.task;

import lombok.RequiredArgsConstructor;
import org.dharbar.telegabot.bot.MessageHelper;
import org.dharbar.telegabot.bot.TelegramBot;
import org.dharbar.telegabot.service.rate.RateFacadeService;
import org.dharbar.telegabot.service.rate.dto.RateDto;
import org.dharbar.telegabot.service.rate.dto.RateProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@ConditionalOnProperty(name = "telegram.my.chat-id")
@Service
@RequiredArgsConstructor
public class BotTaskService {

    private final TelegramBot telegramBot;
    private final RateFacadeService rateService;

    @Value("${telegram.my.chat-id}")
    private Long chatId;

    @Scheduled(cron = "${task.rate-message}")
    public void sendRates() {
        Map<RateProvider, List<RateDto>> currencyRates = rateService.getFiatCurrencyRates();
        Map<RateProvider, List<RateDto>> cryptoRates = rateService.getCryptoRates();

        String currencyRateMessage = MessageHelper.rateMessage(currencyRates);
        String cryptoRatesMessage = MessageHelper.rateMessage(cryptoRates);
        telegramBot.sendMessage(chatId, String.format("%s\n\n%s", currencyRateMessage, cryptoRatesMessage));
    }
}
