package com.softcaretech.excelapp.model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum TransactionType {
    RECEIPT("Receipt"),
    SELL("Sell"),
    CORRECTION("Correction"),
    DAMAGE("Damage");

    private final String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
    public static List<String> getDisplayNames() {
        return Stream.of(TransactionType.values())
                .map(TransactionType::getDisplayName)
                .collect(Collectors.toList());
    }
}
