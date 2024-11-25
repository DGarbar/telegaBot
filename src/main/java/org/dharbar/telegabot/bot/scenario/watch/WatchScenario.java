package org.dharbar.telegabot.bot.scenario.watch;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dharbar.telegabot.bot.scenario.CommandScenario;
import org.dharbar.telegabot.bot.scenario.ScenarioResult;
import org.dharbar.telegabot.job.BinanceP2pAlertPriceJobService;
import org.dharbar.telegabot.job.jobs.BinanceP2pAlertPriceJob.AlertPriceJobData;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static org.dharbar.telegabot.bot.scenario.CommandScenario.createInlineButton;

@Service
@RequiredArgsConstructor
public class WatchScenario implements CommandScenario {

    public static final String WATCH_START_COMMAND = "Watch";

    public static final String WATCH_START_WITH_COMMAND = "/watch";
    public static final String WATCH_DEFAULT_INFO_COMMAND = "/watchDefaultInfo";
    public static final String WATCH_DEFAULT_COMMAND = "/watchDefault";
    public static final String WATCH_MODIFY_DEFAULT_COMMAND = "/watchModifyDefault";
    public static final String WATCH_END_COMMAND = "/watchEnd";
    public static final String WATCH_CUSTOM_COMMAND = "/watchCustom";
    public static final int MIN_AMOUNT = 10000;

    private final BinanceP2pAlertPriceJobService jobService;

    // TODO to db
    private static double defaultPrice = 40.31;
    private static int secondsRetry = 3000;

    public ScenarioResult handle(String messageText, String prevCommand, Long chatId) {
        if (WATCH_START_COMMAND.equals(messageText)) {
            return ScenarioResult.of("Specify function", watchMarkup());

        } else if (WATCH_END_COMMAND.equals(messageText)) {
            jobService.watchEnd(chatId);
            return ScenarioResult.of("Watch disabled");

        } else if (WATCH_DEFAULT_COMMAND.equals(messageText)) {
            watchTargetPrice(chatId, defaultPrice, secondsRetry, MIN_AMOUNT);

            return ScenarioResult.of("Watch configured with target price: %s and retry time: %d seconds. and amount more than %s".formatted(defaultPrice, secondsRetry, MIN_AMOUNT));

        } else if (WATCH_DEFAULT_INFO_COMMAND.equals(messageText)) {
            return ScenarioResult.of("Default price: %s and retry time: %d seconds.".formatted(defaultPrice, secondsRetry));

        } else if (WATCH_MODIFY_DEFAULT_COMMAND.equals(messageText)) {
            return ScenarioResult.of("Specify new default price and retry time in format like 40.3 3000", WATCH_MODIFY_DEFAULT_COMMAND);

        } else if (StringUtils.startsWith(messageText, WATCH_CUSTOM_COMMAND)) {
            String[] splitedMessage = messageText.split(" ");
            double targetPrice = Double.parseDouble(splitedMessage[1]);
            int secondsRetry = Integer.parseInt(splitedMessage[2]);
            watchTargetPrice(chatId, targetPrice, secondsRetry, MIN_AMOUNT);

            return ScenarioResult.of("Watch configured with target price: %s and retry time: %d seconds.".formatted(targetPrice, secondsRetry));

        } else if (StringUtils.startsWith(prevCommand, WATCH_MODIFY_DEFAULT_COMMAND)) {
            String[] splitedMessage = messageText.split(" ");
            defaultPrice = Double.parseDouble(splitedMessage[0]);
            secondsRetry = Integer.parseInt(splitedMessage[1]);
            return ScenarioResult.of("Default price and retry time updated");
        }

        return ScenarioResult.of("Unknown command: " + messageText);
    }

    private void watchTargetPrice(Long chatId, double targetPrice, int secondsRetry, double minAmount) {
        AlertPriceJobData alertPriceJobData = AlertPriceJobData.builder()
                .chatId(chatId)
                .targetPrice(targetPrice)
                .minAmount(minAmount)
                .build();
        jobService.watchTargetPrice(alertPriceJobData, secondsRetry);
    }

    @Override
    public boolean canHandle(String messageText, String prevCommand) {
        return StringUtils.equals(messageText, WATCH_START_COMMAND)
                || StringUtils.startsWith(messageText, WATCH_START_WITH_COMMAND)
                || (StringUtils.startsWith(prevCommand, WATCH_START_WITH_COMMAND) && StringUtils.isNotBlank(messageText));
    }

    public static InlineKeyboardMarkup watchMarkup() {
        InlineKeyboardButton defaultWatch = createInlineButton("Default", WATCH_DEFAULT_COMMAND);
        InlineKeyboardButton defaultInfo = createInlineButton("Default Info", WATCH_DEFAULT_COMMAND);
        InlineKeyboardButton modifyDefault = createInlineButton("Modify Default", WATCH_MODIFY_DEFAULT_COMMAND);
        InlineKeyboardButton watchEnd = createInlineButton("End watch", WATCH_END_COMMAND);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(List.of(
                List.of(defaultInfo, defaultWatch, modifyDefault),
                List.of(watchEnd)));
        return inlineKeyboardMarkup;
    }
}
