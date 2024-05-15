package org.dharbar.telegabot.service.rate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static org.dharbar.telegabot.service.rate.dto.ProviderType.CRYPTO;
import static org.dharbar.telegabot.service.rate.dto.ProviderType.FIAT;

@AllArgsConstructor
@Getter
public enum RateProvider {
    MONO(FIAT), NBU(FIAT), BINANCE(CRYPTO);

   private final ProviderType providerType;
}
