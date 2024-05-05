package org.dharbar.telegabot.service.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            String response = toResponse(messageText);

            Long chatId = message.getChatId();
            sendMessage(chatId, response);
        }
    }

    private String toResponse(String messageText) {
        return switch (messageText) {
            case "/start" -> "Hi!";
            case "/binance" -> {
                Map<RateProvider, List<RateDto>> cryptoRates = rateService.getCryptoRates();
                yield MessageHelper.rateMessage(cryptoRates);
            }
            case "/rate" -> {
                Map<RateProvider, List<RateDto>> providerToRates = rateService.getCurrencyRates();
                yield MessageHelper.rateMessage(providerToRates);
            }
            default -> "Error";
        };
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
