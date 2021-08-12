package com.app.bizlinked.models.enums;

public enum OrderScreenStatusEnum {

    RECEIVED("RECEIVED"),
    PLACED("PLACED"),
    DRAFT("DRAFT");


    String value;
    OrderScreenStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
