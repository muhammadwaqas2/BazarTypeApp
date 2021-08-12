package com.app.bizlinked.models.enums;

public enum LinkStatusEnum {

    SENT("SENT"),
    RECEIVED("RECEIVED"),
    LINKED("LINKED"),
    REJECTED("REJECTED"),
    CANCELLED("CANCELLED"),
    UNLINKED("UNLINKED");


    String value;
    LinkStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
