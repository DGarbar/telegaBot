package org.dharbar.telegabot.utils.mapper;

import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper(componentModel = "spring")
public interface StripBigDecimalMapper {

    default BigDecimal toBigDecimal(BigDecimal value) {
        if (value == null) {
            return null;
        }
        BigDecimal result = value.stripTrailingZeros();
        return result.scale() < 0 ? result.setScale(0, RoundingMode.HALF_UP) : result;
    }
}
