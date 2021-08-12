package com.app.bizlinked.models.db;

import android.os.Parcelable;

import com.app.bizlinked.BizLinkedApplicationClass;
import com.app.bizlinked.db.AppDBHelper;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.helpers.preference.BasePreferenceHelper;
import com.app.bizlinked.helpers.realm.FindDBHelper;
import com.app.bizlinked.helpers.ui.dialogs.DateFormatHelper;
import com.app.bizlinked.listener.custom.Convertable;
import com.app.bizlinked.models.db.base_class.BaseEntity;
import com.app.bizlinked.models.db.base_class.IBaseEntity;
import com.app.bizlinked.models.sync_response.CategorySyncResponse;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class ProductCategory extends RealmObject implements IBaseEntity, Convertable<CategorySyncResponse> {


    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    private BaseEntity baseEntity;

    private String title = "";
    private Image image = null;
    private ProductCategory parentCategory = null;
    private String desc = "";

    public ProductCategory() {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public ProductCategory getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(ProductCategory parentCategory) {
        this.parentCategory = parentCategory;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

        if(getImage() != null)
            jsonObject.addProperty("imageId", getImage().getId());

        if(getParentCategory() != null)
            jsonObject.addProperty("parentId", getParentCategory().getId());

        if(!Utils.isEmptyOrNull(getTitle()))
            jsonObject.addProperty("name", getTitle());

        if(BizLinkedApplicationClass.getPreference() != null && !Utils.isEmptyOrNull(BizLinkedApplicationClass.getPreference().getProfileId()))
            jsonObject.addProperty("companyId", BizLinkedApplicationClass.getPreference().getProfileId());

        if(!Utils.isEmptyOrNull(getDesc()))
            jsonObject.addProperty("description", getDesc());

        jsonObject.addProperty("id", getId());
        jsonObject.addProperty("isDeleted", getDeleted());
        jsonObject.addProperty("lastModified", DateFormatHelper.convertDateToServerFormattedString(getLastModifiedDate()));

        return jsonObject;

    }

    @Override
    public void decode(CategorySyncResponse decodeAbleClass) {


        //Parent Category
        if(!Utils.isEmptyOrNull(decodeAbleClass.getParentId())){
            ProductCategory productCategory = FindDBHelper.byPrimaryKey(AppDBHelper.getRealmInstance(), ProductCategory.class, decodeAbleClass.getParentId());
            if(productCategory != null){
                setParentCategory(productCategory);
            }
        }


        //Image
        if(!Utils.isEmptyOrNull(decodeAbleClass.getImageId())){
            Image image = FindDBHelper.byPrimaryKey(AppDBHelper.getRealmInstance(), Image.class, decodeAbleClass.getImageId());
            if(image != null){
                setImage(image);
            }
        }

        // Name
        if(!Utils.isEmptyOrNull(decodeAbleClass.getName()))
            setTitle(decodeAbleClass.getName());

        // Desc
        if(!Utils.isEmptyOrNull(decodeAbleClass.getDescription()))
            setDesc(decodeAbleClass.getDescription());


        // isDirty
        setDirty(false);

        // isDeleted
        if(decodeAbleClass.getDeleted() != null)
            setDeleted(decodeAbleClass.getDeleted());

        if(!Utils.isEmptyOrNull(decodeAbleClass.getLastModified()))
            setLastModifiedDate(DateFormatHelper.convertStringToServerFormattedDate(decodeAbleClass.getLastModified()));

    }

}