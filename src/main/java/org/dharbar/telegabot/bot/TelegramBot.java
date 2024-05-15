package org.dharbar.telegabot.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dharbar.telegabot.bot.scenario.CommandScenario;
import org.dharbar.telegabot.bot.scenario.ScenarioResult;
import org.dharbar.telegabot.job.JobService;
import org.dharbar.telegabot.service.rate.RateService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final JobService jobService;
    private final RateService rateService;

    private final List<CommandScenario> commandScenarios;

    private final Map<Long, String> chatIdToState = new HashMap<>();

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
        if (update.hasMessage()) {
            Message message = update.getMessage();
            String messageText = message.getText();
            Long chatId = message.getChatId();
            respondTo(chatId, messageText);
            return;
        }

        if (update.hasCallbackQuery()) {
            String messageText = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            respondTo(chatId, messageText);
        }
    }

    private void respondTo(Long chatId, String messageText) {
        try {
            String prevCommand = chatIdToState.get(chatId);
            commandScenarios.stream()
                    .filter(commandScenario -> commandScenario.canHandle(messageText, prevCommand))
                    .findFirst()
                    .ifPresentOrElse(
                            commandScenario -> handleScenarioResult(chatId, messageText, commandScenario, prevCommand),
                            () -> {
                                log.warn("Unknown command: {}", messageText);
                                sendMessage(chatId, "I don't understand you.");
                            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            sendMessage(chatId, "Something went wrong. Please try again later.");
        }
    }

    private void handleScenarioResult(Long chatId, String messageText, CommandScenario commandScenario, String prevCommand) {
        ScenarioResult result = commandScenario.handle(messageText, prevCommand, chatId);
        chatIdToState.put(chatId, result.getCommandState());
        sendMessage(chatId, result.getMessage(), result.getReplyKeyboard());
    }

    public void sendMessage(Long chatId, String textToSend) {
        sendMessage(chatId, textToSend, null);
    }

    public void sendMessage(Long chatId, String textToSend, ReplyKeyboard replyKeyboard) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        sendMessage.setParseMode("HTML");

        if (replyKeyboard != null) {
            sendMessage.setReplyMarkup(replyKeyboard);
        }

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage(), e);
        }
    }
}
