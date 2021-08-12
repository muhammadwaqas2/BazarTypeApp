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
import com.app.bizlinked.models.sync_response.OrderStatusSyncResponse;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class OrderStatus extends RealmObject implements IBaseEntity, Convertable<OrderStatusSyncResponse> {


    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    private BaseEntity baseEntity;


    private Order order;
    private Date submitted;
    private Boolean isApproved;
    private Date approvalDate;
    private Date delivered;
    private Date received;
    private String orderLedgerTransactionId;


    public OrderStatus() {
        this.baseEntity = new BaseEntity();
    }

    public BaseEntity getBaseEntity() {
        return baseEntity;
    }

    public void setBaseEntity(BaseEntity baseEntity) {
        this.baseEntity = baseEntity;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Date getSubmitted() {
        return submitted;
    }

    public void setSubmitted(Date submitted) {
        this.submitted = submitted;
    }

    public Boolean getApproved() {
        return isApproved;
    }

    public void setApproved(Boolean approved) {
        isApproved = approved;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    public Date getDelivered() {
        return delivered;
    }

    public void setDelivered(Date delivered) {
        this.delivered = delivered;
    }

    public Date getReceived() {
        return received;
    }

    public void setReceived(Date received) {
        this.received = received;
    }

    public String getOrderLedgerTransactionId() {
        return orderLedgerTransactionId;
    }

    public void setOrderLedgerTransactionId(String orderLedgerTransactionId) {
        this.orderLedgerTransactionId = orderLedgerTransactionId;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
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

        JsonObject jsonObject = new JsonObject();

        if(getApprovalDate() != null)
            jsonObject.addProperty("approvalDate", DateFormatHelper.convertDateToServerFormattedString(getApprovalDate()));

        if(getDelivered() != null)
            jsonObject.addProperty("delivered", DateFormatHelper.convertDateToServerFormattedString(getDelivered()));

        if(getDelivered() != null)
            jsonObject.addProperty("delivered", DateFormatHelper.convertDateToServerFormattedString(getDelivered()));

        if(getApproved() != null)
            jsonObject.addProperty("isApproved", getApproved());


        if(getOrder() != null && !Utils.isEmptyOrNull(getOrder().getId()))
            jsonObject.addProperty("orderId", getOrder().getId());

        if(getReceived() != null)
            jsonObject.addProperty("received", DateFormatHelper.convertDateToServerFormattedString(getReceived()));

        if(getSubmitted() != null)
            jsonObject.addProperty("submitted", DateFormatHelper.convertDateToServerFormattedString(getSubmitted()));

        if(!Utils.isEmptyOrNull(getOrderLedgerTransactionId()))
            jsonObject.addProperty("orderLedgerTransactionId", getOrderLedgerTransactionId());


        jsonObject.addProperty("id", getId());
        jsonObject.addProperty("isDeleted", getDeleted());
        jsonObject.addProperty("lastModified", DateFormatHelper.convertDateToServerFormattedString(getLastModifiedDate()));

        return jsonObject;

    }

    @Override
    public void decode(OrderStatusSyncResponse decodeAbleClass) {

        //Order
        if(!Utils.isEmptyOrNull(decodeAbleClass.getOrderId())){
            Order ord = FindDBHelper.byPrimaryKey(AppDBHelper.getRealmInstance(), Order.class, decodeAbleClass.getOrderId());
            if(ord != null){
                setOrder(ord);
            }
        }

        // orderLedgerTransactionId
        if(!Utils.isEmptyOrNull(decodeAbleClass.getOrderLedgerTransactionId()))
            setOrderLedgerTransactionId(decodeAbleClass.getOrderLedgerTransactionId());


        //Submitted
        if(!Utils.isEmptyOrNull(decodeAbleClass.getSubmitted()))
            setSubmitted(DateFormatHelper.convertStringToServerFormattedDate(decodeAbleClass.getSubmitted()));

        //approvalDate
        if(!Utils.isEmptyOrNull(decodeAbleClass.getApprovalDate()))
            setApprovalDate(DateFormatHelper.convertStringToServerFormattedDate(decodeAbleClass.getApprovalDate()));

        //delivered
        if(!Utils.isEmptyOrNull(decodeAbleClass.getDelivered()))
            setDelivered(DateFormatHelper.convertStringToServerFormattedDate(decodeAbleClass.getDelivered()));

        //received
        if(!Utils.isEmptyOrNull(decodeAbleClass.getReceived()))
            setReceived(DateFormatHelper.convertStringToServerFormattedDate(decodeAbleClass.getReceived()));


        // isApproved
        if(decodeAbleClass.getApproved() != null)
            setApproved(decodeAbleClass.getApproved());


        // isDirty
        setDirty(false);

        // isDeleted
        if(decodeAbleClass.getDeleted() != null)
            setDeleted(decodeAbleClass.getDeleted());

        if(!Utils.isEmptyOrNull(decodeAbleClass.getLastModified()))
            setLastModifiedDate(DateFormatHelper.convertStringToServerFormattedDate(decodeAbleClass.getLastModified()));

    }

}
