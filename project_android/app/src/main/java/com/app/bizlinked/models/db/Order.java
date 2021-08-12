package com.app.bizlinked.models.db;

import com.app.bizlinked.BizLinkedApplicationClass;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.db.AppDBHelper;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.helpers.realm.FindDBHelper;
import com.app.bizlinked.helpers.ui.dialogs.DateFormatHelper;
import com.app.bizlinked.listener.custom.Convertable;
import com.app.bizlinked.models.db.base_class.BaseEntity;
import com.app.bizlinked.models.db.base_class.IBaseEntity;
import com.app.bizlinked.models.sync_response.CategorySyncResponse;
import com.app.bizlinked.models.sync_response.OrderDetailSyncResponse;
import com.app.bizlinked.models.sync_response.OrderSyncResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Order extends RealmObject implements IBaseEntity, Convertable<OrderSyncResponse> {


    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    private BaseEntity baseEntity;


    private Date initiationDate;
    private String initiatingCompanyID;
    private String supplierCompanyID;
    private String customerCompanyID;
    private Boolean isRejected;
    private RealmList<OrderDetail> orderDetails = null;


    public Order() {
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


    public Date getInitiationDate() {
        return initiationDate;
    }

    public void setInitiationDate(Date initiationDate) {
        this.initiationDate = initiationDate;
    }

    public String getInitiatingCompanyID() {
        return initiatingCompanyID;
    }

    public void setInitiatingCompanyID(String initiatingCompanyID) {
        this.initiatingCompanyID = initiatingCompanyID;
    }

    public String getSupplierCompanyID() {
        return supplierCompanyID;
    }

    public void setSupplierCompanyID(String supplierCompanyID) {
        this.supplierCompanyID = supplierCompanyID;
    }

    public String getCustomerCompanyID() {
        return customerCompanyID;
    }

    public void setCustomerCompanyID(String customerCompanyID) {
        this.customerCompanyID = customerCompanyID;
    }

    public Boolean getRejected() {
        return isRejected;
    }

    public void setRejected(Boolean rejected) {
        isRejected = rejected;
    }

    public RealmList<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(RealmList<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    @Override
    public JsonElement encode() {

        JsonObject jsonObject = new JsonObject();

        //customerCompanyId
        if(!Utils.isEmptyOrNull(getCustomerCompanyID()))
            jsonObject.addProperty("customerCompanyId", getCustomerCompanyID());

        //initiatingCompanyId
        if(!Utils.isEmptyOrNull(getInitiatingCompanyID()))
            jsonObject.addProperty("initiatingCompanyId", getInitiatingCompanyID());

        //initiationDate
        if(getInitiationDate() != null)
            jsonObject.addProperty("initiationDate", DateFormatHelper.convertDateToServerFormattedString(getInitiationDate()));

        //rejected
        if(getRejected() != null)
            jsonObject.addProperty("rejected", getRejected());

        //supplierCompanyId
        if(!Utils.isEmptyOrNull(getSupplierCompanyID()))
            jsonObject.addProperty("supplierCompanyId", getSupplierCompanyID());


        //orderDetail
        JsonArray details = new JsonArray();
        for(OrderDetail orderDetail : getOrderDetails()){
            details.add(orderDetail.encode());
        }
        if(details.size() > 0){
            jsonObject.add("orderDetail", details);
        }

        jsonObject.addProperty("id", getId());
        jsonObject.addProperty("isDeleted", getDeleted());
        jsonObject.addProperty("lastModified", DateFormatHelper.convertDateToServerFormattedString(getLastModifiedDate()));

        return jsonObject;

    }

    @Override
    public void decode(OrderSyncResponse decodeAbleClass) {


        //initiationDate
        if(!Utils.isEmptyOrNull(decodeAbleClass.getInitiationDate()))
            setInitiationDate(DateFormatHelper.convertStringToServerFormattedDate(decodeAbleClass.getInitiationDate()));

        //initiatingCompanyID
        if(!Utils.isEmptyOrNull(decodeAbleClass.getInitiatingCompanyId()))
            setInitiatingCompanyID(decodeAbleClass.getInitiatingCompanyId());

        //supplierCompanyID
        if(!Utils.isEmptyOrNull(decodeAbleClass.getSupplierCompanyId()))
            setSupplierCompanyID(decodeAbleClass.getSupplierCompanyId());

        //customerCompanyID
        if(!Utils.isEmptyOrNull(decodeAbleClass.getCustomerCompanyId()))
            setCustomerCompanyID(decodeAbleClass.getCustomerCompanyId());

        //isRejected
        if(decodeAbleClass.getRejected() != null)
            setRejected(decodeAbleClass.getRejected());


        if(getOrderDetails() != null && getOrderDetails().size() > 0){
            getOrderDetails().clear();
        }

        for (OrderDetailSyncResponse item : decodeAbleClass.getOrderDetail()) {

            OrderDetail orderDetail = null;

            OrderDetail detail = FindDBHelper.byPrimaryKey(AppDBHelper.getRealmInstance(), OrderDetail.class, item.getId());
            if(detail != null){
                orderDetail = detail;
            }else {

                if(AppDBHelper.getRealmInstance().isInTransaction()){
                    orderDetail = AppDBHelper.getRealmInstance().createObject(OrderDetail.class, item.getId());
                }else{
                    orderDetail = new OrderDetail();
                    orderDetail.setId(item.getId());
                }
            }

            orderDetail.decode(item);
            //Append in List
            getOrderDetails().add(orderDetail);

        }


        // isDirty
        setDirty(false);

        // isDeleted
        if(decodeAbleClass.getDeleted() != null)
            setDeleted(decodeAbleClass.getDeleted());

        if(!Utils.isEmptyOrNull(decodeAbleClass.getLastModified()))
            setLastModifiedDate(DateFormatHelper.convertStringToServerFormattedDate(decodeAbleClass.getLastModified()));

    }

}
