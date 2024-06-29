package org.dharbar.telegabot.client.ttingo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class TiingoQuoteResponse {

    private double adjClose;
    private double adjHigh;
    private double adjLow;
    private double adjOpen;
    private long adjVolume;

    private double close;

    private ZonedDateTime date;

    private double divCash;

    private double high;
    private double low;
    private double open;

    @JsonProperty("splitFactor")
    private double splitFactor;
    private long volume;
}
