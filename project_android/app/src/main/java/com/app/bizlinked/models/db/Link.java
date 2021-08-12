package com.app.bizlinked.models.db;

import com.app.bizlinked.BizLinkedApplicationClass;
import com.app.bizlinked.db.AppDBHelper;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.helpers.realm.FindDBHelper;
import com.app.bizlinked.helpers.ui.dialogs.DateFormatHelper;
import com.app.bizlinked.listener.custom.Convertable;
import com.app.bizlinked.models.db.base_class.BaseEntity;
import com.app.bizlinked.models.db.base_class.IBaseEntity;
import com.app.bizlinked.models.sync_response.LinkSyncResponse;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Link extends RealmObject implements IBaseEntity, Convertable<LinkSyncResponse> {

    @PrimaryKey
    private String id = UUID.randomUUID().toString();


    private Profile initiatorCompany;
    private Profile linkedCompany;
    private String linkedCompanyRelation;
    private String status;
    private String supplierCompanyId;
    private String customerCompanyId;

    private BaseEntity baseEntity;

    public Link() {
        this.baseEntity = new BaseEntity();
    }

    public Profile getInitiatorCompany() {
        return initiatorCompany;
    }

    public void setInitiatorCompany(Profile initiatorCompany) {
        this.initiatorCompany = initiatorCompany;
    }

    public Profile getLinkedCompany() {
        return linkedCompany;
    }

    public void setLinkedCompany(Profile linkedCompany) {
        this.linkedCompany = linkedCompany;
    }

    public String getLinkedCompanyRelation() {
        return linkedCompanyRelation;
    }

    public void setLinkedCompanyRelation(String linkedCompanyRelation) {
        this.linkedCompanyRelation = linkedCompanyRelation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSupplierCompanyId() {
        return supplierCompanyId;
    }

    public void setSupplierCompanyId(String supplierCompanyId) {
        this.supplierCompanyId = supplierCompanyId;
    }

    public String getCustomerCompanyId() {
        return customerCompanyId;
    }

    public void setCustomerCompanyId(String customerCompanyId) {
        this.customerCompanyId = customerCompanyId;
    }

    public String getId() {
        return id;
    }

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

        //initiatorCompanyId
        if(getInitiatorCompany() != null && !Utils.isEmptyOrNull(getInitiatorCompany().getId()))
            jsonObject.addProperty("initiatorCompanyId", getInitiatorCompany().getId());

        //supplierCompanyId
        if(!Utils.isEmptyOrNull(getSupplierCompanyId()))
            jsonObject.addProperty("supplierCompanyId", getSupplierCompanyId());

        //customerCompanyId
        if(!Utils.isEmptyOrNull(getCustomerCompanyId()))
            jsonObject.addProperty("customerCompanyId", getCustomerCompanyId());

        //status
        if(!Utils.isEmptyOrNull(getStatus()))
            jsonObject.addProperty("status", getStatus());

        jsonObject.addProperty("id", getId());
        jsonObject.addProperty("isDeleted", getDeleted());
        jsonObject.addProperty("lastModified", DateFormatHelper.convertDateToServerFormattedString(getLastModifiedDate()));

        return jsonObject;
    }

    @Override
    public void decode(LinkSyncResponse decodeAbleClass) {


        //LinkedCompany
        if(linkedCompany != null) {
            linkedCompany.decode(decodeAbleClass.getLinkedCompany());
        }else {
            Profile linked = FindDBHelper.byPrimaryKey(AppDBHelper.getRealmInstance(), Profile.class, decodeAbleClass.getLinkedCompany().getId());
            if(linked != null) {

                setLinkedCompany(linked);

                if(!AppDBHelper.getRealmInstance().isInTransaction()) {
                    AppDBHelper.getRealmInstance().executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            linkedCompany.decode(decodeAbleClass.getLinkedCompany());
                        }
                    });
                }else{ //Extra Ahsan Added
                    linkedCompany.decode(decodeAbleClass.getLinkedCompany());
                }
            }else {
                linked = AppDBHelper.getRealmInstance().createObject(Profile.class, decodeAbleClass.getLinkedCompany().getId());
                linked.decode(decodeAbleClass.getLinkedCompany());
                setLinkedCompany(linked);
            }

        }


        //InitiatorCompany
        if(BizLinkedApplicationClass.getPreference() != null && !Utils.isEmptyOrNull(BizLinkedApplicationClass.getPreference().getProfileId())) {
            if(decodeAbleClass.getInitiatorCompanyId().equalsIgnoreCase(BizLinkedApplicationClass.getPreference().getProfileId())) {
                Profile initiatorCompany = FindDBHelper.byPrimaryKey(AppDBHelper.getRealmInstance(), Profile.class, BizLinkedApplicationClass.getPreference().getProfileId());
                setInitiatorCompany(initiatorCompany);
            }else if(decodeAbleClass.getInitiatorCompanyId().equalsIgnoreCase(linkedCompany.getId())){
                setInitiatorCompany(getLinkedCompany());
            }
        }

        //LinkedCompanyRelation
        if(!Utils.isEmptyOrNull(decodeAbleClass.getLinkedCompanyRelation())) {
            setLinkedCompanyRelation(decodeAbleClass.getLinkedCompanyRelation());
        }

        //customerCompanyId
        if(!Utils.isEmptyOrNull(decodeAbleClass.getCustomerCompanyId())) {
            setCustomerCompanyId(decodeAbleClass.getCustomerCompanyId());
        }

        //customerCompanyId
        if(!Utils.isEmptyOrNull(decodeAbleClass.getCustomerCompanyId())) {
            setCustomerCompanyId(decodeAbleClass.getCustomerCompanyId());
        }

        //supplierCompanyId
        if(!Utils.isEmptyOrNull(decodeAbleClass.getSupplierCompanyId())) {
            setSupplierCompanyId(decodeAbleClass.getSupplierCompanyId());
        }

        //status
        if(!Utils.isEmptyOrNull(decodeAbleClass.getStatus())) {
            setStatus(decodeAbleClass.getStatus());
        }


        setDirty(false);
        setDeleted(decodeAbleClass.getDeleted());
        setLastModifiedDate(DateFormatHelper.convertStringToServerFormattedDate(decodeAbleClass.getLastModified()));
    }
}
