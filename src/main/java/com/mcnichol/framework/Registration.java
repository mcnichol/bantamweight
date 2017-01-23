package com.mcnichol.framework;

import java.util.List;

public class Registration {
    private String mapTo;
    private String type;
    private boolean singleton;
    private List<Constructor> constructorParams;

    public String getMapTo() {
        return mapTo;
    }

    public void setMapTo(String mapTo) {
        this.mapTo = mapTo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    public List<Constructor> getConstructorParams() {
        return constructorParams;
    }

    public void setConstructorParams(List<Constructor> constructorParams) {
        this.constructorParams = constructorParams;
    }


}
