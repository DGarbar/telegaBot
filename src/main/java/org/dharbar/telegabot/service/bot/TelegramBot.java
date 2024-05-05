package org.dharbar.telegabot.service.bot;

import lombok.RequiredArgsConstructor;
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
            Long chatId = message.getChatId();

            switch (messageText) {
                case "/start":
                    sendMessage(chatId, "Hi!");
                    break;

                case "/rate":
                    Map<RateProvider, List<RateDto>> providerToRates = rateService.getCurrencyRates();
                    String response = MessageHelper.rateMessage(providerToRates);
                    sendMessage(chatId, response);
                    break;

                default:
                    sendMessage(chatId, "Error");
            }
        }
    }

    public void sendMessage(Long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {

        }
    }
}
