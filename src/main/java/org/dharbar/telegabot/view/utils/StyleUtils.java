package org.dharbar.telegabot.view.utils;

import java.math.BigDecimal;

public class StyleUtils {

    public static String toPercentageProfitStyle(BigDecimal profitPercentage) {
        if (profitPercentage == null) {
            return "";
        }
        return switch (profitPercentage.compareTo(BigDecimal.valueOf(2))) {
            case 1 -> profitPercentage.compareTo(BigDecimal.valueOf(5)) > 0 ? "good-profit" : "profit";
            case -1 -> profitPercentage.compareTo(BigDecimal.valueOf(-5)) > 0 ? "loss" : "big-loss";
            default -> "";
        };
    }

    public static String toTickerStyle(String ticker) {
        return ticker == null ? "" : ticker.toLowerCase();
    }
}
