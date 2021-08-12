package com.app.bizlinked.models.db;

import com.app.bizlinked.BizLinkedApplicationClass;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.helpers.ui.dialogs.DateFormatHelper;
import com.app.bizlinked.listener.custom.Convertable;
import com.app.bizlinked.models.db.base_class.BaseEntity;
import com.app.bizlinked.models.db.base_class.IBaseEntity;
import com.app.bizlinked.models.sync_response.ImageSyncResponse;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Image extends RealmObject implements IBaseEntity, Convertable<ImageSyncResponse> {

    @PrimaryKey
    private String id = UUID.randomUUID().toString();

    private BaseEntity baseEntity;
    private byte[] data;
    private String companyId;
    private String filePath;
    private Boolean isUploaded = false;
    private Boolean isImageAvailable = false;

    public Image() {
        this.baseEntity = new BaseEntity();
    }


    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public Boolean getUploaded() {
        return isUploaded;
    }

    public void setUploaded(Boolean uploaded) {
        isUploaded = uploaded;
    }

    public Boolean getImageAvailable() {
        return isImageAvailable;
    }

    public void setImageAvailable(Boolean imageAvailable) {
        isImageAvailable = imageAvailable;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
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

        if(BizLinkedApplicationClass.getPreference() != null && !Utils.isEmptyOrNull(BizLinkedApplicationClass.getPreference().getProfileId()))
            jsonObject.addProperty("companyId", BizLinkedApplicationClass.getPreference().getProfileId());

        jsonObject.addProperty("id", getId());
        jsonObject.addProperty("filePath", getFilePath());
        jsonObject.addProperty("isDeleted", getDeleted());
        jsonObject.addProperty("lastModified", DateFormatHelper.convertDateToServerFormattedString(getLastModifiedDate()));

        return jsonObject;
    }

    @Override
    public void decode(ImageSyncResponse decodeAbleClass) {

        setCompanyId(decodeAbleClass.getId());
        setFilePath(decodeAbleClass.getFilePath());
        setImageAvailable(decodeAbleClass.getImageAvailable());
        setUploaded(true);
        setDirty(false);
        setDeleted(decodeAbleClass.getDeleted());
        setLastModifiedDate(DateFormatHelper.convertStringToServerFormattedDate(decodeAbleClass.getLastModified()));
    }

}
