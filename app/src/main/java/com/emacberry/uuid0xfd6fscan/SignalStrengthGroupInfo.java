package com.emacberry.uuid0xfd6fscan;

public class SignalStrengthGroupInfo {
    public int size;
    public RssiRangeType type;

    public SignalStrengthGroupInfo(RssiRangeType type, int size) {
        this.type = type;
        this.size = size;
    }
}
