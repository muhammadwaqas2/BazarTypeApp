package com.app.bizlinked.models.db;

import com.app.bizlinked.BizLinkedApplicationClass;
import com.app.bizlinked.db.AppDBHelper;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.helpers.realm.FindDBHelper;
import com.app.bizlinked.helpers.ui.dialogs.DateFormatHelper;
import com.app.bizlinked.listener.custom.Convertable;
import com.app.bizlinked.models.db.base_class.BaseEntity;
import com.app.bizlinked.models.db.base_class.IBaseEntity;
import com.app.bizlinked.models.sync_response.CategorySyncResponse;
import com.app.bizlinked.models.sync_response.OrderDetailSyncResponse;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class OrderDetail extends RealmObject implements IBaseEntity, Convertable<OrderDetailSyncResponse> {


    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    private BaseEntity baseEntity;

    private String orderId;
    private String productId;
    private Integer quantity = 0;
    private Double price = 0.0;
    private String productName;
    private String productCategoryId;
    private String productCategoryName;
    private String productImageId;

    public OrderDetail() {
        this.baseEntity = new BaseEntity();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BaseEntity getBaseEntity() {
        return baseEntity;
    }

    public void setBaseEntity(BaseEntity baseEntity) {
        this.baseEntity = baseEntity;
    }

    @Override
    public Date getLastModifiedDate() {
        return baseEntity.getLastModifiedDate();
    }

    @Override
    public void setLastModifiedDate(Date lastModifiedDate) {
        baseEntity.setLastModifiedDate(lastModifiedDate);
    }

    @Override
    public Boolean getDirty() {
        return baseEntity.getDirty();
    }

    @Override
    public void setDirty(Boolean dirty) {
        baseEntity.setDirty(dirty);
    }

    @Override
    public Boolean getDeleted() {
        return baseEntity.getDeleted();
    }

    @Override
    public void setDeleted(Boolean deleted) {
        baseEntity.setDeleted(deleted);
    }


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(String productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public String getProductCategoryName() {
        return productCategoryName;
    }

    public void setProductCategoryName(String productCategoryName) {
        this.productCategoryName = productCategoryName;
    }

    public String getProductImageId() {
        return productImageId;
    }

    public void setProductImageId(String productImageId) {
        this.productImageId = productImageId;
    }

    @Override
    public JsonElement encode() {

        JsonObject jsonObject = new JsonObject();


        //orderId
        if(!Utils.isEmptyOrNull(getOrderId()))
            jsonObject.addProperty("orderId", getOrderId());

        //price
        if(getPrice() != null)
            jsonObject.addProperty("price", getPrice());

        //productCategoryId
        if(!Utils.isEmptyOrNull(getProductCategoryId()))
            jsonObject.addProperty("productCategoryId", getProductCategoryId());

        //productCategoryName
        if(!Utils.isEmptyOrNull(getProductCategoryName()))
            jsonObject.addProperty("productCategoryName", getProductCategoryName());

        //productId
        if(!Utils.isEmptyOrNull(getProductId()))
            jsonObject.addProperty("productId", getProductId());

        //productImageId
        if(!Utils.isEmptyOrNull(getProductImageId()))
            jsonObject.addProperty("productImageId", getProductImageId());

        //productName
        if(!Utils.isEmptyOrNull(getProductName()))
            jsonObject.addProperty("productName", getProductName());

        //quantity
        if(getQuantity() != null)
            jsonObject.addProperty("quantity", getQuantity());

        jsonObject.addProperty("id", getId());
        jsonObject.addProperty("isDeleted", getDeleted());
        jsonObject.addProperty("lastModified", DateFormatHelper.convertDateToServerFormattedString(getLastModifiedDate()));

        return jsonObject;

    }

    @Override
    public void decode(OrderDetailSyncResponse decodeAbleClass) {

        //orderId
        if(!Utils.isEmptyOrNull(decodeAbleClass.getOrderId()))
            setOrderId(decodeAbleClass.getOrderId());

        //price
        if(decodeAbleClass.getPrice() != null)
            setPrice(decodeAbleClass.getPrice());

        //productCategoryId
        if(!Utils.isEmptyOrNull(decodeAbleClass.getProductCategoryId()))
            setProductCategoryId(decodeAbleClass.getProductCategoryId());

        //productCategoryName
        if(!Utils.isEmptyOrNull(decodeAbleClass.getProductCategoryName()))
            setProductCategoryName(decodeAbleClass.getProductCategoryName());

        //productId
        if(!Utils.isEmptyOrNull(decodeAbleClass.getProductId()))
            setProductId(decodeAbleClass.getProductId());

        //productImageId
        if(!Utils.isEmptyOrNull(decodeAbleClass.getProductImageId()))
            setProductImageId(decodeAbleClass.getProductImageId());

        //productName
        if(!Utils.isEmptyOrNull(decodeAbleClass.getProductName()))
            setProductName(decodeAbleClass.getProductName());

        //quantity
        if(decodeAbleClass.getQuantity() != null)
            setQuantity(decodeAbleClass.getQuantity());


//        //id
//        if(!Utils.isEmptyOrNull(decodeAbleClass.getId()))
//            setId(decodeAbleClass.getId());


        // isDirty
        setDirty(false);

        // isDeleted
        if(decodeAbleClass.getDeleted() != null)
            setDeleted(decodeAbleClass.getDeleted());

        if(!Utils.isEmptyOrNull(decodeAbleClass.getLastModified()))
            setLastModifiedDate(DateFormatHelper.convertStringToServerFormattedDate(decodeAbleClass.getLastModified()));

    }

}
