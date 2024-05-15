package org.dharbar.telegabot.bot.scenario.rate;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dharbar.telegabot.bot.MessageHelper;
import org.dharbar.telegabot.bot.scenario.CommandScenario;
import org.dharbar.telegabot.bot.scenario.ScenarioResult;
import org.dharbar.telegabot.service.rate.RateService;
import org.dharbar.telegabot.service.rate.dto.RateDto;
import org.dharbar.telegabot.service.rate.dto.RateProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RateScenario implements CommandScenario {

    // Keyboard doesn't support override to normal name
    public static final String UAH_FIAT_RATE_COMMAND = "Fiat rate";
    public static final String UAH_CRYPTO_RATE_COMMAND = "Crypto rate";

    private final RateService rateService;

    @Override
    public ScenarioResult handle(String messageText, String prevCommand, Long chatId) {
        if (messageText.equals(UAH_FIAT_RATE_COMMAND)) {
            Map<RateProvider, List<RateDto>> providerToRates = rateService.getFiatCurrencyRates();
            String message = MessageHelper.rateMessage(providerToRates);
            return ScenarioResult.of(message);

        } else if (messageText.equals(UAH_CRYPTO_RATE_COMMAND)) {
            Map<RateProvider, List<RateDto>> cryptoRates = rateService.getCryptoRates();
            String message = MessageHelper.rateMessage(cryptoRates);
            return ScenarioResult.of(message);
        }
        return ScenarioResult.of("Unknown command: " + messageText);
    }

    @Override
    public boolean canHandle(String messageText, String prevCommand) {
        return StringUtils.equalsAny(messageText, UAH_FIAT_RATE_COMMAND, UAH_CRYPTO_RATE_COMMAND);
    }
}
