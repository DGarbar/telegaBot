package org.dharbar.telegabot.bot.scenario.other;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dharbar.telegabot.bot.scenario.CommandScenario;
import org.dharbar.telegabot.bot.scenario.ScenarioResult;
import org.dharbar.telegabot.facade.NotionStatementFacade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Objects;

import static org.dharbar.telegabot.bot.scenario.CommandScenario.createInlineButton;

@Service
@RequiredArgsConstructor
public class OtherScenario implements CommandScenario {

    public static final String OTHER_START_COMMAND = "Other";

    public static final String OTHER_START_WITH_COMMAND = "/other";
    public static final String OTHER_NOTION_COMMAND = "/otherNotionUpdate";

    @Value("${telegram.my.chat-id}")
    Long myChatId;

    private final NotionStatementFacade notionStatementService;

    @Override
    public ScenarioResult handle(String messageText, String prevCommand, Long chatId) {
        if (!Objects.equals(chatId, myChatId)) {
            return ScenarioResult.of("You are not allowed to use this command");
        }

        if (StringUtils.equals(messageText, OTHER_START_COMMAND)) {
            return ScenarioResult.of("Specify function", otherMarkup());

        } else if (StringUtils.equals(messageText, OTHER_NOTION_COMMAND)) {
            notionStatementService.updateStatementTable();
            return ScenarioResult.of("Notion updated");
        }

        return ScenarioResult.of("Unknown command: " + messageText);
    }

    @Override
    public boolean canHandle(String messageText, String prevCommand) {
        return StringUtils.equals(messageText, OTHER_START_COMMAND)
                || StringUtils.startsWith(messageText, OTHER_START_WITH_COMMAND);
    }

    public static InlineKeyboardMarkup otherMarkup() {
        InlineKeyboardButton updateNotion = createInlineButton("Update Notion", OTHER_NOTION_COMMAND);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(List.of(
                List.of(updateNotion)));
        return inlineKeyboardMarkup;
    }
}
