package com.app.bizlinked.models.db;

import com.app.bizlinked.BizLinkedApplicationClass;
import com.app.bizlinked.db.AppDBHelper;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.helpers.realm.FindDBHelper;
import com.app.bizlinked.helpers.ui.dialogs.DateFormatHelper;
import com.app.bizlinked.listener.custom.Convertable;
import com.app.bizlinked.models.db.base_class.BaseEntity;
import com.app.bizlinked.models.db.base_class.IBaseEntity;
import com.app.bizlinked.models.sync_response.BusinessCategorySyncResponse;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class BusinessCategory extends RealmObject implements IBaseEntity, Convertable<BusinessCategorySyncResponse> {

    @PrimaryKey
    private String id = UUID.randomUUID().toString();

    private BaseEntity baseEntity;

    private String name;

    public BusinessCategory() {
        this.baseEntity = new BaseEntity();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return this.id;
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

        if(!Utils.isEmptyOrNull(getName()))
            jsonObject.addProperty("name", getName());

        if(BizLinkedApplicationClass.getPreference() != null && !Utils.isEmptyOrNull(BizLinkedApplicationClass.getPreference().getProfileId()))
            jsonObject.addProperty("companyId", BizLinkedApplicationClass.getPreference().getProfileId());


        jsonObject.addProperty("id", getId());
        jsonObject.addProperty("isDeleted", getDeleted());
        jsonObject.addProperty("lastModified", DateFormatHelper.convertDateToServerFormattedString(getLastModifiedDate()));

        return jsonObject;
    }

    @Override
    public void decode(BusinessCategorySyncResponse decodeAbleClass) {

        // Name
        if(!Utils.isEmptyOrNull(decodeAbleClass.getName()))
            setName(decodeAbleClass.getName());

        // isDirty
        setDirty(false);

        // isDeleted
        if(decodeAbleClass.getDeleted() != null)
            setDeleted(decodeAbleClass.getDeleted());

        if(!Utils.isEmptyOrNull(decodeAbleClass.getLastModified()))
            setLastModifiedDate(DateFormatHelper.convertStringToServerFormattedDate(decodeAbleClass.getLastModified()));

    }

}
