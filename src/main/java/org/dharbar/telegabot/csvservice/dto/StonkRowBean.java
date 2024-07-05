package org.dharbar.telegabot.csvservice.dto;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvDate;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class StonkRowBean {
    @CsvBindByPosition(position = 1)
    String ticker;

    @CsvDate(value = "dd.MM.yy")
    @CsvBindByPosition(position = 2)
    LocalDate buyDateAt;

    @CsvBindByPosition(position = 3)
    BigDecimal buyQuantity;

    @CsvBindByPosition(position = 4)
    BigDecimal buyTotalUsd;

    @CsvBindByPosition(position = 5)
    BigDecimal buyRate;

    @CsvBindByPosition(position = 7)
    BigDecimal buyCommissionUsd;

    @CsvDate(value = "dd.MM.yy")
    @CsvBindByPosition(position = 10)
    LocalDate sellDateAt;

    @CsvBindByPosition(position = 11)
    BigDecimal sellRate;

    @CsvBindByPosition(position = 13)
    BigDecimal sellCommissionUsd;
}
