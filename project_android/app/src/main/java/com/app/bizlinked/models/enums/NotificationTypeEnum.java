package com.app.bizlinked.models.enums;

public enum NotificationTypeEnum {

    //Link
    LINK_REQUEST_ADD("LINK_REQUEST_ADD"),
    LINK_REQUEST_ACCEPTED("LINK_REQUEST_ACCEPTED"),
    LINK_REQUEST_SYNC("LINK_REQUEST_SYNC"),

    //Order
    ORDER_SUBMITTED("ORDER_SUBMITTED"),
    ORDER_SYNC("ORDER_SYNC"),

    ORDER_APPROVED_REJECTED("ORDER_APPROVED_REJECTED"),
    ORDER_DELIVERED("ORDER_DELIVERED"),
    ORDER_RECEIVED("ORDER_RECEIVED");


    String value;
    NotificationTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
