package com.app.bizlinked.models.viewmodel;

import android.arch.lifecycle.MutableLiveData;

import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.db.AppDBHelper;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.helpers.sync_helpers.ImageSyncUtility;
import com.app.bizlinked.listener.custom.DatabaseTransactionInterface;
import com.app.bizlinked.models.db.Link;
import com.app.bizlinked.models.db.OrderDetail;
import com.app.bizlinked.webhelpers.WebApiRequest;

import java.util.Date;
import java.util.HashMap;

import io.realm.Realm;
import okhttp3.ResponseBody;

public class OrderDetailViewModel extends BaseViewModal {


    private OrderDetail orderDetail;
    private String productName;
    private String productCategory;
    private Double price;
    private Integer quantity;
    private MutableLiveData<byte[]> prodImage = null;

    public OrderDetail getOrderDetail() {
        return orderDetail;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public Double getPrice() {
        return price;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public MutableLiveData<byte[]> getProdImage(){

        if(prodImage == null){
            prodImage = new MutableLiveData<>();
        }
        return prodImage;
    }



    public void init(OrderDetail detail) {
        this.orderDetail = detail;
        populateViewModel();
    }

    private void populateViewModel() {
        this.productName = this.orderDetail.getProductName();
        this.productCategory = this.orderDetail.getProductCategoryName();
        this.price = this.orderDetail.getPrice();
        this.quantity = this.orderDetail.getQuantity();
    }

    public void updateImage(){
        if(!Utils.isEmptyOrNull(this.orderDetail.getProductImageId()) && (getProdImage().getValue() == null || getProdImage().getValue().length <= 0)){
            downloadImage(this.orderDetail.getProductImageId());
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

                        getProdImage().setValue(imageData);
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



    public void updateQuantity(DatabaseTransactionInterface databaseTransactionInterface){
        try {
            AppDBHelper.getRealmInstance().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    getOrderDetail().setQuantity(getQuantity());
                    getOrderDetail().setLastModifiedDate(new Date());
                    getOrderDetail().setDirty(true);

                    databaseTransactionInterface.onSuccessTransaction();
                }
            });
        }catch (Exception e){
            databaseTransactionInterface.onErrorTransaction();
        }
    }

}
