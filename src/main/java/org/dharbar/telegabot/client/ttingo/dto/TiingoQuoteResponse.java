package org.dharbar.telegabot.client.ttingo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
public class TiingoQuoteResponse {

    private double adjClose;
    private double adjHigh;
    private double adjLow;
    private double adjOpen;
    private long adjVolume;

    private ZonedDateTime date;

    private double divCash;

    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal open;
    private BigDecimal close;

    @JsonProperty("splitFactor")
    private double splitFactor;
    private long volume;
}
