package com.emacberry.uuid0xfd6fscan;

import androidx.annotation.NonNull;

public enum RssiRangeType{GOOD(0), NEAR(0), MEDIUM(1), FAR(2), BAD(-1);
    public final int value;
    RssiRangeType(final int newValue) {
        value = newValue;
    }

    @NonNull
    @Override
    public String toString() {
        switch (value){
            default:
            case 0:
                return "GOOD/NEAR";
            case 1:
                return "MEDIUM";
            case 2:
                return "FAR";
            case -1:
                return "BAD";
        }
    }
}