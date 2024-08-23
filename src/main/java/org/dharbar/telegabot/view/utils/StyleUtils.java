package org.dharbar.telegabot.view.utils;

import java.math.BigDecimal;

public class StyleUtils {

    public static String toProfitStyle(BigDecimal amount) {
        if (amount == null) {
            return "";
        }
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            return "";
        }
        return switch (amount.compareTo(BigDecimal.valueOf(2))) {
            case 1 -> amount.compareTo(BigDecimal.valueOf(5)) > 0 ? "good-profit" : "profit";
            case -1 -> amount.compareTo(BigDecimal.valueOf(-5)) > 0 ? "loss" : "big-loss";
            default -> "";
        };
    }

    public static String toTickerStyle(String ticker) {
        return ticker == null ? "" : ticker.toLowerCase();
    }
}
