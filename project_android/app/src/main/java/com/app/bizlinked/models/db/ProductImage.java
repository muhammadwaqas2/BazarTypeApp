package com.app.bizlinked.models.db;

import com.app.bizlinked.BizLinkedApplicationClass;
import com.app.bizlinked.db.AppDBHelper;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.helpers.realm.FindDBHelper;
import com.app.bizlinked.helpers.ui.dialogs.DateFormatHelper;
import com.app.bizlinked.listener.custom.Convertable;
import com.app.bizlinked.models.db.base_class.BaseEntity;
import com.app.bizlinked.models.db.base_class.IBaseEntity;
import com.app.bizlinked.models.sync_response.ProductImageSyncResponse;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ProductImage extends RealmObject implements IBaseEntity, Convertable<ProductImageSyncResponse> {

    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    private BaseEntity baseEntity;
    private String productID = "";
    private Image image = null;


    public ProductImage() {
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

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
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

        if(!Utils.isEmptyOrNull(getProductID()))
            jsonObject.addProperty("productId", getProductID());


        jsonObject.addProperty("id", getId());
        jsonObject.addProperty("isDeleted", getDeleted());
        jsonObject.addProperty("lastModified", DateFormatHelper.convertDateToServerFormattedString(getLastModifiedDate()));

        return jsonObject;

    }

    @Override
    public void decode(ProductImageSyncResponse decodeAbleClass) {

        //Image
        if(!Utils.isEmptyOrNull(decodeAbleClass.getImageId())){
            Image image = FindDBHelper.byPrimaryKey(AppDBHelper.getRealmInstance(), Image.class, decodeAbleClass.getImageId());
            if(image != null){
                setImage(image);
            }
        }

        // ProductId
        if(!Utils.isEmptyOrNull(decodeAbleClass.getProductId()))
            setProductID(decodeAbleClass.getProductId());

        // isDirty
        setDirty(false);

        // isDeleted
        if(decodeAbleClass.getDeleted() != null && decodeAbleClass.getDeleted())
            setDeleted(decodeAbleClass.getDeleted());
        else
            setDeleted(false);

        if(!Utils.isEmptyOrNull(decodeAbleClass.getLastModified()))
            setLastModifiedDate(DateFormatHelper.convertStringToServerFormattedDate(decodeAbleClass.getLastModified()));


        // Sync Work Deleted Case
        if(!getDeleted()){

            Product product = FindDBHelper.byPrimaryKey(AppDBHelper.getRealmInstance(), Product.class, decodeAbleClass.getProductId());
            if(product != null){
                if(product.getImages().where().equalTo("id", decodeAbleClass.getProductId()).findAll() .size() > 0){

                    //Ask From Noman
                    for (int i = 0; i < product.getImages().size(); i++) {
                        if(product.getImages().get(i).getId().equalsIgnoreCase(getId())){
                            product.getImages().set(i, this);
                        }
                    }
                }else{
                    product.getImages().add(this);
                }


            }

        }

    }

}