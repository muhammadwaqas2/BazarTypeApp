package com.app.bizlinked.models.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.widget.Toast;

import com.app.bizlinked.activities.baseClass.BaseActivity;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.db.AppDBHelper;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.helpers.realm.RealmLiveData;
import com.app.bizlinked.helpers.sync_helpers.SyncManager;
import com.app.bizlinked.models.db.Image;
import com.app.bizlinked.models.db.Product;
import com.app.bizlinked.models.db.ProductCategory;
import com.app.bizlinked.models.db.ProductImage;
import com.app.bizlinked.models.enums.EntityEnum;
import com.app.bizlinked.models.enums.OrderScreenStatusEnum;
import com.app.bizlinked.models.server_response.CategoryListResponse;
import com.app.bizlinked.models.server_response.ProductListResponse;
import com.app.bizlinked.models.sync_response.CategorySyncResponse;
import com.app.bizlinked.models.sync_response.ProductImageSyncResponse;
import com.app.bizlinked.models.sync_response.ProductSyncResponse;
import com.app.bizlinked.webhelpers.WebApiRequest;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;

import io.realm.RealmResults;

public class ProductCategoryListViewModal extends ViewModel {

    private RealmResults<ProductCategory> resultCategories;
    private RealmResults<Product> resultProducts;

    private BaseActivity activityReference = null;

    //For Server things
    MutableLiveData<ArrayList<CategoryViewModal>> categoriesServerData = null;
    MutableLiveData<ArrayList<ProductViewModal>> productServerData = null;
    int page = 0;
    int totalPages = 0;
    String companyID = null;
    String productCategoryId = null;
    OrderScreenStatusEnum orderScreenStatus = null;


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public RealmLiveData<ProductCategory> getAllCategories(String categoryID){
        resultCategories = getAllCategoriesFromDB(categoryID);
        return new RealmLiveData<>(resultCategories);
        // Async runs the fetch off the main thread, and returns
        // results as LiveData back on the main.

    }

    public MutableLiveData<ArrayList<CategoryViewModal>> getAllCategoriesDataFromServer(String companyID, String categoryID){
        this.companyID = companyID;
        this.productCategoryId = categoryID;
        if(categoriesServerData == null){
            categoriesServerData = new MutableLiveData<>();
        }
        return categoriesServerData;
    }

    public MutableLiveData<ArrayList<ProductViewModal>> getAllProductDataFromServer(String companyID, String categoryID){
        this.companyID = companyID;
        this.productCategoryId = categoryID;
        if(productServerData == null){
            productServerData = new MutableLiveData<>();
        }
        return productServerData;
    }

    public BaseActivity getActivityReference() {
        return activityReference;
    }

    public void setActivityReference(BaseActivity activityReference) {
        this.activityReference = activityReference;
    }

    public String getCompanyID() {
        return companyID;
    }


    public String getProductCategoryId() {
        return productCategoryId;
    }

    public RealmResults<ProductCategory> getAllSelectionCategoriesFromDB(String parentCatId, String selectedCategoryID){

        RealmResults<ProductCategory> categories = null;

        if(Utils.isEmptyOrNull(parentCatId) && Utils.isEmptyOrNull(selectedCategoryID)){

            categories = AppDBHelper.getRealmInstance().where(ProductCategory.class)
                        .beginGroup()
                            .isNull("parentCategory")
                            .and()
                            .equalTo("baseEntity.isDeleted", false)
                        .endGroup()
                    .findAll();

        }else if(!Utils.isEmptyOrNull(parentCatId) && !Utils.isEmptyOrNull(selectedCategoryID)) {

            categories = AppDBHelper.getRealmInstance().where(ProductCategory.class)
                        .beginGroup()
                            .notEqualTo("id", selectedCategoryID)
                            .and()
                            .equalTo("parentCategory.id", parentCatId)
                            .and()
                            .equalTo("baseEntity.isDeleted", false)
                        .endGroup()
                    .findAll();


        }else  if(!Utils.isEmptyOrNull(parentCatId) && Utils.isEmptyOrNull(selectedCategoryID)) {

            categories = AppDBHelper.getRealmInstance().where(ProductCategory.class)
                        .beginGroup()
                            .equalTo("parentCategory.id", parentCatId)
                            .and()
                            .equalTo("baseEntity.isDeleted", false)
                        .endGroup()
                    .findAll();

        }else if(Utils.isEmptyOrNull(parentCatId) && !Utils.isEmptyOrNull(selectedCategoryID)){

            categories = AppDBHelper.getRealmInstance().where(ProductCategory.class)
                        .beginGroup()
                            .notEqualTo("id", selectedCategoryID)
                            .and()
                            .isNull("parentCategory")
                            .and()
                            .equalTo("baseEntity.isDeleted", false)
                        .endGroup()
                    .findAll();
        }

        return categories;
    }

    public RealmResults<ProductCategory> getAllCategoriesFromDB(String categoryID){

        RealmResults<ProductCategory> categories;

        if(!Utils.isEmptyOrNull(categoryID)){

            categories = AppDBHelper.getRealmInstance().where(ProductCategory.class)
                    .beginGroup()
                    .equalTo("parentCategory.id", categoryID)
                    .and()
                    .equalTo("baseEntity.isDeleted", false)
                    .endGroup()
                    .findAll();

        }else {
            categories = AppDBHelper.getRealmInstance().where(ProductCategory.class)
                    .beginGroup()
                    .isNull("parentCategory")
                    .and()
                    .equalTo("baseEntity.isDeleted", false)
                    .endGroup()
                    .findAll();
        }

        return categories;
    }


//    func getAllProducts(categoryID: String?) -> Results<Product>?{
//        let products: Results<Product>?
//        if let id = categoryID{
//            products = AppManager.sharedInstance.realm?.objects(Product.self).filter("category.id == %@ && isDeleted == false", id)
//        }
//        else{
//            products = AppManager.sharedInstance.realm?.objects(Product.self).filter("category == nil && isDeleted == false")
//        }
//        return products
//    }
//    func getAllImages(productID: String) -> Results<ProductImage>?{
//        return AppManager.sharedInstance.realm?.objects(ProductImage.self).filter("productID == \(productID)")
//    }


    public RealmLiveData<Product> getAllProducts(String categoryID){
        resultProducts = getAllProductsFromDB(categoryID);
        return new RealmLiveData<>(resultProducts);
    }



    public RealmResults<Product> getAllProductsFromDB(String categoryID){

        RealmResults<Product> products;

        if(!Utils.isEmptyOrNull(categoryID)){

            products = AppDBHelper.getRealmInstance().where(Product.class)
                        .beginGroup()
                            .equalTo("category.id", categoryID)
                            .and()
                            .equalTo("baseEntity.isDeleted", false)
                        .endGroup()
                    .findAll();

        }else {

            products = AppDBHelper.getRealmInstance().where(Product.class)
                        .beginGroup()
                            .isNull("category")
                            .and()
                            .equalTo("baseEntity.isDeleted", false)
                        .endGroup()
                    .findAll();
        }

        return products;

    }

    public ProductImage getAllImages(String productID){
//        return FindDBHelper.byKey(AppDBHelper.getRealmInstance(), ProductImage.class, "productID", productID);
        return AppDBHelper.getRealmInstance().where(ProductImage.class).equalTo("productID", productID).findFirst();
    }



    public void getDataFromServer(boolean isRefresh){
        if(categoriesServerData.getValue() == null || isRefresh){
            this.page = 0;
            getCategoriesFromServer();
        }
    }

    private void getCategoriesFromServer() {

        HashMap<String, Object> params = new HashMap<>();

        params.put("companyId", getCompanyID());
        if(!Utils.isEmptyOrNull(productCategoryId)){
            params.put("productCategoryId", productCategoryId);
        }else{
            //because of API restriction send empty string
            params.put("productCategoryId", "");
        }

        WebApiRequest.getInstance(activityReference, true).getCompanyCategoryListFromServer(AppConstant.ServerAPICalls.productCategoryListURL, params, new WebApiRequest.APIRequestDataCallBack() {
            @Override
            public void onSuccess(JsonElement response) {

                if(response != null && response.isJsonObject()){

                    JsonObject responseObject = response.getAsJsonObject();
                    Gson gson = new Gson();

                    //Parsing Gson
                    CategoryListResponse categoryListResponse = gson.fromJson(responseObject, new TypeToken<CategoryListResponse>() {}.getType());

                    ArrayList<CategoryViewModal> catViewModelList = new ArrayList<>();

                    for (CategorySyncResponse cat: categoryListResponse.getContent()) {


                        ProductCategory category = new ProductCategory();
                        category.setId(cat.getId());
                        category.decode(cat);
                        if(cat.getImage() != null){
                            Image catImage = new Image();
                            catImage.setId(cat.getImage().getId());
                            catImage.decode(cat.getImage());
                            category.setImage(catImage);
                        }

                        CategoryViewModal categoryViewModal = new CategoryViewModal();
                        categoryViewModal.init(category, true);

                        //category view model list Add
                        catViewModelList.add(categoryViewModal);
                    }

                    categoriesServerData.setValue(catViewModelList);
                    getProductsFromServer();
                }
            }

            @Override
            public void onError(String errorResponse) {

            }

            @Override
            public void onNoNetwork() {

            }
        });

    }

    public void getProductsFromServer() {

        HashMap<String, Object> params = new HashMap<>();

        params.put("companyId", getCompanyID());
        if(!Utils.isEmptyOrNull(productCategoryId)){
            params.put("productCategoryId", productCategoryId);
        }else{
            //because of API restriction send empty string
            params.put("productCategoryId", "");
        }
        params.put("page", page);
        params.put("size", AppConstant.CONFIGURATION.PAGE_SIZE);

        WebApiRequest.getInstance(activityReference, true).getCompanyProductListFromServer(AppConstant.ServerAPICalls.productListURL, params, new WebApiRequest.APIRequestDataCallBack() {
            @Override
            public void onSuccess(JsonElement response) {

                if(response != null && response.isJsonObject()){

                    JsonObject responseObject = response.getAsJsonObject();
                    Gson gson = new Gson();

                    //Parsing Gson
                    ProductListResponse productListResponse = gson.fromJson(responseObject, new TypeToken<ProductListResponse>() {}.getType());

                    ArrayList<ProductViewModal> prodViewModelList = new ArrayList<>();

                    for (ProductSyncResponse prod : productListResponse.getContent()) {


                        Product product = new Product();
                        product.setId(prod.getId());
                        product.decode(prod);

                        for (ProductImageSyncResponse prodImgResp: prod.getProductImages()) {

                            //ProductImage Object for Product
                            ProductImage prodImg = new ProductImage();
                            prodImg.setId(prodImgResp.getId());
                            prodImg.decode(prodImgResp);

                            //Image Object for Product Image
                            Image img = new Image();
                            img.setId(prodImgResp.getImage().getId());
                            img.decode(prodImgResp.getImage());
                            prodImg.setImage(img);

                            //Set in Product table info
                            product.getImages().add(prodImg);

                        }

                        ProductViewModal productViewModal = new ProductViewModal();
                        productViewModal.init(product, true);
                        //category view model list Add
                        prodViewModelList.add(productViewModal);
                    }

                    productServerData.setValue(prodViewModelList);
                    totalPages = productListResponse.getTotalPages();
                }
            }

            @Override
            public void onError(String errorResponse) {

            }

            @Override
            public void onNoNetwork() {

            }
        });

    }

    public void syncProducts(){
        SyncManager.getInstance().addEntityToQueue(EntityEnum.Image);
        SyncManager.getInstance().addEntityToQueue(EntityEnum.ProductCategory);
        SyncManager.getInstance().addEntityToQueue(EntityEnum.Product);
        SyncManager.getInstance().addEntityToQueue(EntityEnum.ProductImage);
        SyncManager.getInstance().addEntityToQueue(EntityEnum.ImageSyncUtility);
    }

}
