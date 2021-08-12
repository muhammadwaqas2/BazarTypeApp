package com.app.bizlinked.models.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;
import android.widget.ImageView;

import com.app.bizlinked.activities.baseClass.BaseActivity;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.db.AppDBHelper;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.helpers.file.FileHelper;
import com.app.bizlinked.helpers.realm.FindDBHelper;
import com.app.bizlinked.helpers.sync_helpers.ImageSyncUtility;
import com.app.bizlinked.helpers.sync_helpers.SyncManager;
import com.app.bizlinked.helpers.ui.dialogs.DateFormatHelper;
import com.app.bizlinked.listener.custom.DatabaseTransactionInterface;
import com.app.bizlinked.listener.custom.DataSaveAndConvertInterface;
import com.app.bizlinked.models.db.Image;
import com.app.bizlinked.models.db.Product;
import com.app.bizlinked.models.db.ProductCategory;
import com.app.bizlinked.models.enums.EntityEnum;
import com.app.bizlinked.webhelpers.WebApiRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;

public class CategoryViewModal extends BaseViewModal {

    private ProductCategory productCategory = null;
    private ProductCategory parentCategory = null;
    private String categoryID;
    private String title;
    private String desc;
    private File imageFile;
    private Image image;
    private Boolean isDeleted = false;
    private Boolean isImageDeleted = false;
    private MutableLiveData<HashMap<String, byte[]>> catImage = null;


    public MutableLiveData<HashMap<String, byte[]>> getCatImage(){

        if(catImage == null){
            catImage = new MutableLiveData<>();
        }
        return catImage;
    }



    public void init(ProductCategory selectedCatObject, Boolean fromServer) {

        this.productCategory = selectedCatObject;
        this.title = selectedCatObject.getTitle();
        if(!Utils.isEmptyOrNull(selectedCatObject.getDesc())){
            this.desc = selectedCatObject.getDesc();
        }

        if(selectedCatObject.getImage() != null){

                if(selectedCatObject.getImage().isManaged() && selectedCatObject.getImage().isValid()){
                    this.image = AppDBHelper.getRealmInstance().copyFromRealm(selectedCatObject.getImage());
                }else {
                    this.image = selectedCatObject.getImage();
                }
        }

        this.categoryID = selectedCatObject.getId();

        if(selectedCatObject.getParentCategory() != null)
            this.parentCategory = AppDBHelper.getRealmInstance().copyFromRealm(selectedCatObject.getParentCategory());

        this.isDeleted = selectedCatObject.getDeleted();



        //For Company Category if its coming from server
        if(this.productCategory.getImage() != null && fromServer){
            if(this.productCategory.getImage().getData() == null && (this.productCategory.getImage().getImageAvailable() != null &&
                                                                    this.productCategory.getImage().getImageAvailable())){
                downloadImage(this.productCategory.getImage().getId());
            }
        }
    }

    public void downloadImage(String imageID){

        String imageDownloadURL = AppConstant.ServerAPICalls.imageDownloadURL + imageID;

        ImageSyncUtility.getInstance().downloadImageRequest(imageDownloadURL, null, new WebApiRequest.ImageRequestDataCallBack() {
            @Override
            public void onSuccess(ResponseBody response) {

                try {
                    if (response != null && response.contentLength() > 0) {

                        byte[] imageData = response.bytes();
                        HashMap<String, byte[]> hashMap = new HashMap<>();
                        hashMap.put(getProductCategory().getId(), imageData);

                        getProductCategory().getImage().setData(imageData);
                        getCatImage().setValue(hashMap); //for live image render
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String response) {

            }

            @Override
            public void onNoNetwork() {

            }
        });
    }



    public ArrayList<ProductCategory> getHierarchy(ProductCategory parentCategory) {

        ProductCategory parent = parentCategory;
        ArrayList<ProductCategory> hierarchy = new ArrayList<>();

        while (parent != null){
            hierarchy.add(parent);
            parent = parent.getParentCategory();
        }
        return hierarchy;
    }


    //Add Category
    public void addCategory(DatabaseTransactionInterface databaseTransactionInterface){

        if(productCategory == null){
            productCategory = new ProductCategory();
        }

        productCategory.setParentCategory(parentCategory);
        productCategory.setTitle(title);
        productCategory.setDesc(desc);

        if(imageFile != null){

            createImageDataObjectForDb(imageFile, new DataSaveAndConvertInterface<Object, Image>() {
                @Override
                public void onSuccess(Object syncResponse, Image imgObject) {
                    productCategory.setImage(imgObject);
                }

                @Override
                public void onError() {

                }
            });
        }

        AppDBHelper.getDBHelper().insertOrUpdateRecordToDB(productCategory, databaseTransactionInterface);


        //TODO; Sync work
        syncCategory();

    }

    //Update Category
    public void updateCategory(DatabaseTransactionInterface databaseTransactionInterface){

        try{

            AppDBHelper.getRealmInstance().beginTransaction();

            productCategory = FindDBHelper.byPrimaryKey(AppDBHelper.getRealmInstance(), ProductCategory.class, categoryID);


            if(parentCategory != null)
                productCategory.setParentCategory(AppDBHelper.getRealmInstance().copyToRealmOrUpdate(parentCategory));
            else
                productCategory.setParentCategory(null);


            productCategory.setTitle(title);
            productCategory.setDesc(desc);
            productCategory.setLastModifiedDate(new Date());
            productCategory.setDirty(true);
            productCategory.setDeleted(isDeleted);


            if(imageFile != null){

                byte[] imageBytesFile = FileHelper.readBytesFromFile(imageFile);

                //If Category Already has image
                if(productCategory.getImage() != null){

                    if (imageBytesFile != null && imageBytesFile.length > 0) {

                        productCategory.getImage().setDeleted(true);
                        productCategory.getImage().setDirty(true);
                        productCategory.getImage().setLastModifiedDate(new Date());

                        Image img = AppDBHelper.getRealmInstance().createObject(Image.class, UUID.randomUUID().toString());
                        img.setData(imageBytesFile);
                        productCategory.setImage(img);

//                        productCategory.getImage().setData(imageBytesFile);
//                        productCategory.getImage().setLastModifiedDate(new Date());
//                        productCategory.getImage().setUploaded(false);
                    }

                }else{

                    //If Category doesn't have image
                    if (imageBytesFile != null && imageBytesFile.length > 0) {

                        Image imgObject = new Image();
                        imgObject.setData(imageBytesFile);
                        productCategory.setImage(AppDBHelper.getRealmInstance().copyToRealmOrUpdate(imgObject));
                    }

                }

            }else if(isImageDeleted){

                //If we remove image from Category
                // remove image work

                if (productCategory.getImage() != null){
                    productCategory.getImage().setDeleted(true);
                    productCategory.getImage().setDirty(true);
                    productCategory.getImage().setLastModifiedDate(new Date());
                    productCategory.setImage(null);
                }

            }

            AppDBHelper.getRealmInstance().insertOrUpdate(productCategory);
            AppDBHelper.getRealmInstance().commitTransaction();
            databaseTransactionInterface.onSuccessTransaction();
        }catch (Exception e){

            e.printStackTrace();
            if(AppDBHelper.getRealmInstance().isInTransaction()){
                AppDBHelper.getRealmInstance().cancelTransaction();
            }
            databaseTransactionInterface.onErrorTransaction();
        }


        //TODO: Sync Work
        syncCategory();
    }

    private void syncCategory() {
        SyncManager.getInstance().addEntityToQueue(EntityEnum.Image);
        SyncManager.getInstance().addEntityToQueue(EntityEnum.ProductCategory);
        SyncManager.getInstance().addEntityToQueue(EntityEnum.ImageSyncUtility);
    }

    public boolean checkForSubOptions(){

        if(productCategory != null){

            RealmResults<ProductCategory> subCats = AppDBHelper.getRealmInstance().where(ProductCategory.class)
                    .equalTo("parentCategory.id", productCategory.getId())
                    .equalTo("baseEntity.isDeleted", false)
                    .findAll();
            RealmResults<Product> products = AppDBHelper.getRealmInstance().where(Product.class)
                    .equalTo("category.id", productCategory.getId())
                    .equalTo("baseEntity.isDeleted", false)
                    .findAll();

            return subCats.isEmpty() && products.isEmpty();
        }
        return false;
    }


    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }

    public ProductCategory getParentCategory() {
//        if(parentCategory != null)
//            return AppDBHelper.getRealmInstance().copyToRealmOrUpdate(parentCategory);
//        else
            return parentCategory;
    }

    public void setParentCategory(ProductCategory parentCategory) {
//        if(parentCategory != null)
//            this.parentCategory = AppDBHelper.getRealmInstance().copyFromRealm(parentCategory);
//        else
//            this.parentCategory = null;

        this.parentCategory = parentCategory;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Boolean getImageDeleted() {
        return isImageDeleted;
    }

    public void setImageDeleted(Boolean imageDeleted) {
        isImageDeleted = imageDeleted;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

}
