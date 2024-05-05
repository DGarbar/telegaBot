package org.dharbar.telegabot.service.statement.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Builder
@Value
public class TransactionDto {
    List<TransactionType> types;
    double value;
    String comment;
    LocalDate date;

}
