package com.app.bizlinked.models.viewmodel;

import android.arch.lifecycle.MutableLiveData;

import com.app.bizlinked.activities.baseClass.BaseActivity;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.db.AppDBHelper;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.models.db.Image;
import com.app.bizlinked.models.db.Product;
import com.app.bizlinked.models.db.ProductCategory;
import com.app.bizlinked.models.db.ProductImage;
import com.app.bizlinked.models.server_response.ProductListResponse;
import com.app.bizlinked.models.sync_response.ProductImageSyncResponse;
import com.app.bizlinked.models.sync_response.ProductSyncResponse;
import com.app.bizlinked.webhelpers.WebApiRequest;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import io.realm.Case;
import io.realm.RealmResults;

public class ProductSearchViewModal  extends BaseViewModal {

    private String companyID;
    private int page = 0;
    private int totalPages = 0;
    private boolean isSearched = false;

    MutableLiveData<ArrayList<ProductViewModal>> searchedProduct = null;

    private BaseActivity activityReference = null;

    public BaseActivity getActivityReference() {
        return activityReference;
    }

    public void setActivityReference(BaseActivity activityReference) {
        this.activityReference = activityReference;
    }



    public MutableLiveData<ArrayList<ProductViewModal>> getSearchedProduct(){

        if(searchedProduct == null)
            searchedProduct = new MutableLiveData<>();

        return searchedProduct;
    }

    private RealmResults<Product> resultProducts;

//    private Product product;
//    private RealmResults<Product> resultProducts;
//    private RealmResults<ProductImage> resultProductImages;


    public String getCompanyID() {
        return companyID;
    }

    public void setCompanyID(String companyID) {
        this.companyID = companyID;
    }

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

    public boolean isSearched() {
        return isSearched;
    }

    public void setSearched(boolean searched) {
        isSearched = searched;
    }


    public void searchProducts(String keyword){

        if(!Utils.isEmptyOrNull(keyword)){
            this.page = 0;

            if(!Utils.isEmptyOrNull(companyID)){
                searchFromServer(keyword);
            }else{
                searchLocal(keyword);
            }
        }
    }

    public void searchFromServer(String keyword) {

        HashMap<String, Object> params = new HashMap<>();

        params.put("companyId", getCompanyID());
        params.put("term", keyword);
        params.put("page", page);
        params.put("size", AppConstant.CONFIGURATION.PAGE_SIZE);

        WebApiRequest.getInstance(activityReference, true).getCompanyProductListFromServer(AppConstant.ServerAPICalls.productsSearchURL, params, new WebApiRequest.APIRequestDataCallBack() {
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
                        //product view model list Add
                        prodViewModelList.add(productViewModal);
                    }

                    isSearched = true;
                    getSearchedProduct().setValue(prodViewModelList);
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

    private void searchLocal(String keyword) {

        resultProducts = getAllProductsFromDB(keyword);
        if(resultProducts != null && resultProducts.size() > 0){

            ArrayList<ProductViewModal> prods = new ArrayList<>();

            for (Product product: resultProducts) {
                ProductViewModal prodVM = new ProductViewModal();
                prodVM.init(product, false);

                //Product View Modelks append
                prods.add(prodVM);
            }


            if(prods.size() > 0){
                getSearchedProduct().setValue(prods);
            }

        }
    }

    public RealmResults<Product> getAllProductsFromDB(String keyword){
        return AppDBHelper.getRealmInstance().where(Product.class)
                    .beginGroup()
                        .contains("title", keyword, Case.INSENSITIVE)
                        .and()
                        .equalTo("baseEntity.isDeleted", false)
                    .endGroup()
                .findAll();
    }


}
