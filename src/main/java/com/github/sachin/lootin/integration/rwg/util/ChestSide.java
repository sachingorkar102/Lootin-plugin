package com.github.sachin.lootin.integration.rwg.util;

public enum ChestSide {

    LEFT,
    RIGHT,
    SINGLE;

    public static ChestSide of(byte id) {
        if (id < 0 || id >= values().length) {
            return ChestSide.SINGLE;
        }
        return values()[id];
    }
}
