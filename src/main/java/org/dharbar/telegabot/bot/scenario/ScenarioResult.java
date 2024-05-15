package org.dharbar.telegabot.bot.scenario;

import lombok.Value;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Value(staticConstructor = "of")
public class ScenarioResult {
    String message;
    ReplyKeyboard replyKeyboard;
    String commandState;

    public static ScenarioResult of(String message) {
        return new ScenarioResult(message, null, null);
    }

    public static ScenarioResult of(String message, String commandStateSave) {
        return new ScenarioResult(message, null, commandStateSave);
    }

    public static ScenarioResult of(String message, ReplyKeyboard replyKeyboard) {
        return new ScenarioResult(message, replyKeyboard, null);
    }
}
