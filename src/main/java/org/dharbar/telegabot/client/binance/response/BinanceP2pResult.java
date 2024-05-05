package org.dharbar.telegabot.client.binance.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BinanceP2pResult {

    private String code;
    private String message;
    private String messageDetail;
    private List<P2pOrder> data;
    private int total;
    private boolean success;

    @Data
    public static class P2pOrder {
        private Advertise adv;
        // private String advertiser;
    }

    @Data
    public static class Advertise {
        private String tradeType;
        // USDT
        private String asset;
        private String fiatUnit;
        private BigDecimal price;
        private BigDecimal surplusAmount;
        private BigDecimal tradableQuantity;
        private BigDecimal maxSingleTransAmount;
        private BigDecimal minSingleTransAmount;
        private List<TradeMethod> tradeMethods;
    }

    @Data
    public static class TradeMethod {
        private String identifier;
        private String tradeMethodName;
    }
}
