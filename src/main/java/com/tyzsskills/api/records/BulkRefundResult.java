package com.tyzsskills.api.records;

public record BulkRefundResult(int levelToRemove, int spToRefund) {
    public static final BulkRefundResult EMPTY = new BulkRefundResult(0, 0);
}
