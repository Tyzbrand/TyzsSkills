package com.tyzsskills.api.records;

public record BulkPurchaseResult(int levelToAdd, int spToWithdraw){
    public static final BulkPurchaseResult EMPTY = new BulkPurchaseResult(0, 0);
}
