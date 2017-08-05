package com.rgy.codebuilder.model;

import javafx.beans.property.StringProperty;

/**
 * Created by Administrator on 2017/8/4.
 */
public class Temp {

    private StringProperty tempId;
    private StringProperty tempName;

    public Temp(StringProperty tempId, StringProperty tempName) {
        this.tempId = tempId;
        this.tempName = tempName;
    }

    public String getTempId() {
        return tempId.get();
    }

    public StringProperty tempIdProperty() {
        return tempId;
    }

    public void setTempId(String tempId) {
        this.tempId.set(tempId);
    }

    public String getTempName() {
        return tempName.get();
    }

    public StringProperty tempNameProperty() {
        return tempName;
    }

    public void setTempName(String tempName) {
        this.tempName.set(tempName);
    }

    @Override
    public String toString() {
        return tempName.getValue();
    }
}
