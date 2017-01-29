package com.mcnichol.framework;


public enum BantamweightMsg {
    FILE_DOES_NOT_EXIST(1), FILE_EXISTS(0);

    private final int value;

    BantamweightMsg(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
