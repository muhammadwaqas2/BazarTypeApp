package com.app.bizlinked.models.db;

import com.app.bizlinked.BizLinkedApplicationClass;
import com.app.bizlinked.db.AppDBHelper;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.helpers.realm.FindDBHelper;
import com.app.bizlinked.helpers.ui.dialogs.DateFormatHelper;
import com.app.bizlinked.listener.custom.Convertable;
import com.app.bizlinked.models.db.base_class.BaseEntity;
import com.app.bizlinked.models.db.base_class.IBaseEntity;
import com.app.bizlinked.models.sync_response.ProductSyncResponse;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

public class Product extends RealmObject implements IBaseEntity, Convertable<ProductSyncResponse> {

    @PrimaryKey
    private String id = UUID.randomUUID().toString();
    private BaseEntity baseEntity;


    private String title = "";
    private RealmList<ProductImage> images = new RealmList<>();
    private String desc = "";
    private Double price = 0.0;
    private Boolean isActive  = false;
    private Boolean isPublished  = false;
    private ProductCategory category = null;


    public Product() {
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

    public RealmList<ProductImage> getImages() {
        return images;
    }

    public void setImages(RealmList<ProductImage> images) {
        this.images = images;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getPublished() {
        return isPublished;
    }

    public void setPublished(Boolean published) {
        isPublished = published;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
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

        //Company Id
        if(BizLinkedApplicationClass.getPreference() != null && !Utils.isEmptyOrNull(BizLinkedApplicationClass.getPreference().getProfileId()))
            jsonObject.addProperty("companyId", BizLinkedApplicationClass.getPreference().getProfileId());

        //Category
        if(getCategory() != null)
            jsonObject.addProperty("categoryId", getCategory().getId());


        //Name
        if(!Utils.isEmptyOrNull(getTitle()))
            jsonObject.addProperty("name", getTitle());

        //Desc
        if(!Utils.isEmptyOrNull(getDesc()))
            jsonObject.addProperty("description", getDesc());

        //Price
        if(getPrice() != null)
            jsonObject.addProperty("price", getPrice());

        //isActive
        if(getActive() != null)
            jsonObject.addProperty("isActive", getActive());


        //isPublished
        if(getPublished() != null)
            jsonObject.addProperty("isPublished", getPublished());


        jsonObject.addProperty("id", getId());
        jsonObject.addProperty("isDeleted", getDeleted());
        jsonObject.addProperty("lastModified", DateFormatHelper.convertDateToServerFormattedString(getLastModifiedDate()));

        return jsonObject;

    }

    @Override
    public void decode(ProductSyncResponse decodeAbleClass) {

        ProductCategory productCategory = null;
        //categoryId
        if(!Utils.isEmptyOrNull(decodeAbleClass.getCategoryId())){
            productCategory = FindDBHelper.byPrimaryKey(AppDBHelper.getRealmInstance(), ProductCategory.class, decodeAbleClass.getCategoryId());
            if(productCategory != null){
                setCategory(productCategory);
            }else{

                //For new Category if server has this category
                if(decodeAbleClass.getProductCategory() != null){

                    if(AppDBHelper.getRealmInstance().isInTransaction()){
                        productCategory = AppDBHelper.getRealmInstance().createObject(ProductCategory.class, decodeAbleClass.getProductCategory().getId());
                    }else{
                        productCategory = new ProductCategory();
                        productCategory.setId(decodeAbleClass.getProductCategory().getId());
                    }

//                    productCategory = AppDBHelper.getRealmInstance().createObject(ProductCategory.class, decodeAbleClass.getProductCategory().getId());
                    productCategory.decode(decodeAbleClass.getProductCategory());
//                    productCategory.setId(decodeAbleClass.getProductCategory().getId());
                    setCategory(productCategory);
                }
            }
        }



//        RealmResults<ProductImage> productImages = AppDBHelper.getRealmInstance().where(ProductImage.class).equalTo("productID", getId()).findAll();
//        if(productImages.size() > 0){
//            RealmList <ProductImage> results = new RealmList<>();
//            results.addAll(productImages);
//            setImages(results);
//        }


        // Title
        if(!Utils.isEmptyOrNull(decodeAbleClass.getName()))
            setTitle(decodeAbleClass.getName());

        // Desc
        if(!Utils.isEmptyOrNull(decodeAbleClass.getDescription()))
            setDesc(decodeAbleClass.getDescription());

        // Price
        if(decodeAbleClass.getPrice() != null)
            setPrice(decodeAbleClass.getPrice());

        // isDirty
        setDirty(false);

        // isActive
        if(decodeAbleClass.getActive() != null)
            setActive(decodeAbleClass.getActive());

       // isPublished
        if(decodeAbleClass.getPublished() != null)
            setPublished(decodeAbleClass.getPublished());

        // isDeleted
        if(decodeAbleClass.getDeleted() != null)
            setDeleted(decodeAbleClass.getDeleted());

        if(!Utils.isEmptyOrNull(decodeAbleClass.getLastModified()))
            setLastModifiedDate(DateFormatHelper.convertStringToServerFormattedDate(decodeAbleClass.getLastModified()));

    }

}

