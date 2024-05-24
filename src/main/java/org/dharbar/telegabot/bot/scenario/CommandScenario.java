package org.dharbar.telegabot.bot.scenario;

import org.springframework.lang.Nullable;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public interface CommandScenario {

    ScenarioResult handle(String messageText, String prevCommand, Long chatId);

    boolean canHandle(String messageText, @Nullable String prevCommand);

    static InlineKeyboardButton createInlineButton(String text, String callbackData) {
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText(text);
        inlineKeyboardButton1.setCallbackData(callbackData);
        return inlineKeyboardButton1;
    }
}
