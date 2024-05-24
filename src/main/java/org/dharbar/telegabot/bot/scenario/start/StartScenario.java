package org.dharbar.telegabot.bot.scenario.start;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dharbar.telegabot.bot.scenario.CommandScenario;
import org.dharbar.telegabot.bot.scenario.ScenarioResult;
import org.dharbar.telegabot.bot.scenario.other.OtherScenario;
import org.dharbar.telegabot.bot.scenario.rate.RateScenario;
import org.dharbar.telegabot.bot.scenario.watch.WatchScenario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StartScenario implements CommandScenario {

    @Value("${telegram.my.chat-id}")
    Long myChatId;

    @Override
    public ScenarioResult handle(String messageText, String prevCommand, Long chatId) {
        return ScenarioResult.of("Hello Hello! I am a bot that can help you with rates and other stuff.", initButtons(chatId));
    }

    public ReplyKeyboard initButtons(Long chatId) {

        // also add new KeyboardButton(OtherScenario.OTHER_START_COMMAND) if chat id is mine
        List<KeyboardButton> buttons = new ArrayList<>(List.of(
                new KeyboardButton(RateScenario.UAH_FIAT_RATE_COMMAND),
                new KeyboardButton(RateScenario.UAH_CRYPTO_RATE_COMMAND),
                new KeyboardButton(WatchScenario.WATCH_START_COMMAND)
        ));

        if (chatId.equals(myChatId)) {
            buttons.add(new KeyboardButton(OtherScenario.OTHER_START_COMMAND));
        }
        KeyboardRow keyboardButtons = new KeyboardRow(buttons);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(List.of(keyboardButtons));
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    @Override
    public boolean canHandle(String messageText, String prevCommand) {
        return StringUtils.startsWith(messageText, "/start");
    }
}
