package org.dharbar.telegabot.client.mono.response;

import lombok.Data;

@Data
public class TransactionMonoResponse {

    private String id;
    private long time;
    private String description;
    private int mcc;
    private int originalMcc;
    // in smallest units
    private int amount;
    private int operationAmount;
    private int currencyCode;
    private int commissionRate;
    private int cashbackAmount;
    private int balance;
    private boolean hold;
    private String receiptId;
}
