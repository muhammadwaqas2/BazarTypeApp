package com.app.bizlinked.models.db;

import com.app.bizlinked.db.AppDBHelper;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.helpers.ui.dialogs.DateFormatHelper;
import com.app.bizlinked.listener.custom.Convertable;
import com.app.bizlinked.models.db.base_class.BaseEntity;
import com.app.bizlinked.models.db.base_class.IBaseEntity;
import com.app.bizlinked.models.sync_response.CompanyPackageSyncResponse;
import com.app.bizlinked.models.sync_response.PackageSyncResponse;
import com.google.gson.JsonElement;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CompanyPackage  extends RealmObject implements IBaseEntity, Convertable<CompanyPackageSyncResponse> {

    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    private BaseEntity baseEntity;

    private String companyId;
    private String packageId;
    private Boolean isActive;
    private Package packg;


    public CompanyPackage() {
        this.baseEntity = new BaseEntity();
    }


    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public BaseEntity getBaseEntity() {
        return baseEntity;
    }

    public void setBaseEntity(BaseEntity baseEntity) {
        this.baseEntity = baseEntity;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Package getPackage() {
        return packg;
    }

    public void setPackage(Package packg) {
        this.packg = packg;
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
    public void decode(CompanyPackageSyncResponse decodeAbleClass) {

        // Company Id
        if(!Utils.isEmptyOrNull(decodeAbleClass.getCompanyId()))
            setCompanyId(decodeAbleClass.getCompanyId());

        // Package Id
        if(!Utils.isEmptyOrNull(decodeAbleClass.getPackageId()))
            setPackageId(decodeAbleClass.getPackageId());

        // is Active
        if(decodeAbleClass.getActive() != null)
            setActive(decodeAbleClass.getActive());


        if(decodeAbleClass.getPackage() != null){
            Package packg = AppDBHelper.getRealmInstance().createObject(Package.class, decodeAbleClass.getPackage().getId());
            packg.decode(decodeAbleClass.getPackage());
//            packg.setId(decodeAbleClass.getPackage().getId());

            //Set Package
            setPackage(packg);
        }


        //Dirty
        setDirty(false);

        //Deleted
        setDeleted(decodeAbleClass.getDeleted());

        //Last Modified
        setLastModifiedDate(DateFormatHelper.convertStringToServerFormattedDate(decodeAbleClass.getLastModified()));

    }
}
