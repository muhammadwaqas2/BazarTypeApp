package com.app.bizlinked.models.enums;

public enum EntityEnum {


    Product("Product", 30),
    ProductCategory("ProductCategory", 20),
    ProductImage("ProductImage", 40),
    Image("Image", 10),
    ImageSyncUtility("ImageSyncUtility", 1000),
    BusinessCategory("BusinessCategory", 500),
    City("City", 501),
    Profile("Profile", 502),
    Link("Link", 5),
    CompanyPackage("CompanyPackage", 504),
    Order("Order", 505),
    OrderStatus("OrderStatus", 506);


    private String entityName;
    private Integer priority;

    EntityEnum(String entityName, Integer priority) {
       this.entityName = entityName;
       this.priority = priority;
    }

    public String getEntityName() {
        return entityName;
    }

    public Integer getPriority() {
        return priority;
    }
}
