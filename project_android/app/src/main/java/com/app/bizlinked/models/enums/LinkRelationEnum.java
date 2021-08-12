package com.app.bizlinked.models.enums;

public enum LinkRelationEnum {

    SUPPLIER("SUPPLIER"),
    CUSTOMER("CUSTOMER");


    String value;
    LinkRelationEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
