package com.app.bizlinked.models.db;

import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.listener.custom.Convertable;
import com.app.bizlinked.models.db.base_class.BaseEntity;
import com.app.bizlinked.models.db.base_class.IBaseEntity;
import com.app.bizlinked.models.sync_response.PackageSyncResponse;
import com.app.bizlinked.models.sync_response.ProductSyncResponse;
import com.google.gson.JsonElement;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Package extends RealmObject implements IBaseEntity, Convertable<PackageSyncResponse> {

    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    private BaseEntity baseEntity;

    private String name = "";
    private String desc = "";
    private Integer orderQuantity = 0;
    private Integer expiryDays = 0;
    private Double price = 0.0;

    public Package() {
        this.baseEntity = new BaseEntity();
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(Integer orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public Integer getExpiryDays() {
        return expiryDays;
    }

    public void setExpiryDays(Integer expiryDays) {
        this.expiryDays = expiryDays;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
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



    @Override
    public JsonElement encode() {
        return null;
    }

    @Override
    public void decode(PackageSyncResponse decodeAbleClass) {

        // Name
        if(!Utils.isEmptyOrNull(decodeAbleClass.getName()))
            setName(decodeAbleClass.getName());

        // Desc
        if(!Utils.isEmptyOrNull(decodeAbleClass.getDescription()))
            setDesc(decodeAbleClass.getDescription());

        // order quantity
        if(decodeAbleClass.getOrderQuantity() != null )
            setOrderQuantity(decodeAbleClass.getOrderQuantity());

        // expiry days
        if(decodeAbleClass.getExpiryDays() != null )
            setExpiryDays(decodeAbleClass.getExpiryDays());

        // Price
        if(decodeAbleClass.getPrice() != null )
            setPrice(decodeAbleClass.getPrice());

    }
}
