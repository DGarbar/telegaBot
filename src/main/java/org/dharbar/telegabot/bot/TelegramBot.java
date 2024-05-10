package org.dharbar.telegabot.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dharbar.telegabot.job.JobService;
import org.dharbar.telegabot.job.jobs.AlertPriceJob.AlertPriceJobData;
import org.dharbar.telegabot.service.rate.RateService;
import org.dharbar.telegabot.service.rate.dto.RateDto;
import org.dharbar.telegabot.service.rate.dto.RateProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final JobService jobService;
    private final RateService rateService;

    @Value("${telegram.name}")
    private String botName;
    @Value("${telegram.token}")
    private String token;

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (update.hasMessage() && message.hasText()) {
            String messageText = message.getText();
            Long chatId = message.getChatId();
            respondTo(chatId, messageText);
        }
    }

    private void respondTo(Long chatId, String messageText) {
        try {
            if (messageText.equals("/start")) {
                sendMessage(chatId, "Hello! I am a bot that can help you with rates and other stuff.");

            } else if (messageText.equals("/binance")) {
                Map<RateProvider, List<RateDto>> cryptoRates = rateService.getCryptoRates();
                String message = MessageHelper.rateMessage(cryptoRates);
                sendMessage(chatId, message);

            } else if (messageText.equals("/rate")) {
                Map<RateProvider, List<RateDto>> providerToRates = rateService.getCurrencyRates();
                String message = MessageHelper.rateMessage(providerToRates);
                sendMessage(chatId, message);

            } else if (messageText.startsWith("/watch")) {
                String[] splitedMessage = messageText.split(" ");
                double targetPrice = Double.parseDouble(splitedMessage[1]);
                AlertPriceJobData alertPriceJobData = AlertPriceJobData.builder()
                        .chatId(chatId)
                        .targetPrice(targetPrice)
                        .build();
                jobService.watchTargetPrice(alertPriceJobData);
            } else {
                sendMessage(chatId, "I don't understand you.");
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            sendMessage(chatId, "Something went wrong. Please try again later.");
        }
    }

    public void sendMessage(Long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage(), e);
        }
    }
}
