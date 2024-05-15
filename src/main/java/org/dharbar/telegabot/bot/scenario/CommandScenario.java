package org.dharbar.telegabot.bot.scenario;

import org.springframework.lang.Nullable;

public interface CommandScenario {

    ScenarioResult handle(String messageText,  String prevCommand, Long chatId);

    boolean canHandle(String messageText, @Nullable String prevCommand);
}
