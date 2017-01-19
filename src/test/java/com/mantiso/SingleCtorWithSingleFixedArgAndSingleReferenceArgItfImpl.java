package com.mantiso;

/**
 * Created by kevinj.
 */
public class SingleCtorWithSingleFixedArgAndSingleReferenceArgItfImpl implements SingleCtorWithSingleFixedArgAndSingleReferenceArgItf {
    private String value;
    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }


    private Logger logger;
    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public void setLogger(Logger logger) {
        this.logger = logger;
    }


    public SingleCtorWithSingleFixedArgAndSingleReferenceArgItfImpl(String value, Logger logger) {

        this.value = value;
        this.logger = logger;
    }
}
