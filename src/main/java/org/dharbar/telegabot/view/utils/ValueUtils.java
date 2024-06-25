package org.dharbar.telegabot.view.utils;

import java.util.Objects;

public class ValueUtils {

    public static String formatValue(Object value) {
        return Objects.toString(value, "");
    }

}
