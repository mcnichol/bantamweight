package com.mantiso;

/**
 * Created by kevinj.
 */
public class SingleCtorWithFixedArgItfImpl implements SingleCtorWithFixedArgItf {
    public SingleCtorWithFixedArgItfImpl(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    private int value;

}
