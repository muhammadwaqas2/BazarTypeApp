package com.app.bizlinked.models.viewmodel;

import android.arch.lifecycle.MutableLiveData;

import com.app.bizlinked.BizLinkedApplicationClass;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.db.AppDBHelper;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.helpers.sync_helpers.ImageSyncUtility;
import com.app.bizlinked.helpers.sync_helpers.SyncManager;
import com.app.bizlinked.listener.custom.DataSaveAndConvertInterface;
import com.app.bizlinked.listener.custom.DatabaseTransactionInterface;
import com.app.bizlinked.models.db.Image;
import com.app.bizlinked.models.db.Link;
import com.app.bizlinked.models.db.Order;
import com.app.bizlinked.models.db.OrderDetail;
import com.app.bizlinked.models.db.OrderStatus;
import com.app.bizlinked.models.db.Product;
import com.app.bizlinked.models.db.ProductCategory;
import com.app.bizlinked.models.db.ProductImage;
import com.app.bizlinked.models.enums.EntityEnum;
import com.app.bizlinked.webhelpers.WebApiRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import io.reactivex.internal.util.ArrayListSupplier;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import okhttp3.ResponseBody;

public class ProductViewModal extends BaseViewModal {


    private Product product;
    private RealmResults<Product> resultProducts;
    private RealmResults<ProductImage> resultProductImages;
//    private ProductImage[] productImages;
////    private var productToken: NotificationToken?
////    private var productImagesToken: NotificationToken?
//    private var listOfImages: [ProductImage] = []
//    private var listOfDeletedImages: [ProductImage] = []
//    private var category: ProductCategory?{
//        didSet{
//            self.categoryID = self.category?.id
//            self.categoryName = self.category?.title
//        }
//    }

    private String title;
    private String desc;
    private Boolean isActive = true;
    private Boolean isPublished = false;
    private ProductCategory category;
    private ArrayList<File> images;
    private ArrayList<ProductImage> listOfDeletedImages = new ArrayList<>();
    //    private ArrayList<Image> productImages;
    private String productID;
    private String price;
    private Boolean isDeleted = false;

    private MutableLiveData<HashMap<String, byte[]>> prodImage = null;


    public MutableLiveData<HashMap<String, byte[]>> getProductImage() {

        if (prodImage == null) {
            prodImage = new MutableLiveData<>();
        }
        return prodImage;
    }


    public void addDeletedProductImages(ProductImage productImage) {
        listOfDeletedImages.add(productImage);
    }

    public void clearDeletedProductImages() {
        listOfDeletedImages.clear();
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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

    public ArrayList<File> getImages() {
        return images;
    }

    public void setImages(ArrayList<File> images) {
        this.images = images;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

//    public String getCategoryID() {
//        return categoryID;
//    }
//
//    public void setCategoryID(String categoryID) {
//        this.categoryID = categoryID;
//    }
//
//    public String getCategoryName() {
//        return categoryName;
//    }
//
//    public void setCategoryName(String categoryName) {
//        this.categoryName = categoryName;
//    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public void downloadImage(ProductImage productImageObject) {

        String imageDownloadURL = AppConstant.ServerAPICalls.imageDownloadURL + productImageObject.getImage().getId();

        ImageSyncUtility.getInstance().downloadImageRequest(imageDownloadURL, null, new WebApiRequest.ImageRequestDataCallBack() {
            @Override
            public void onSuccess(ResponseBody response) {

                try {
                    if (response != null && response.contentLength() > 0) {

                        byte[] imageData = response.bytes();
                        HashMap<String, byte[]> hashMap = new HashMap<>();
                        hashMap.put(productImageObject.getId(), imageData);

                        productImageObject.getImage().setData(imageData);
                        getProductImage().setValue(hashMap); //for live image render

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


    public void init(Product selectedProductObject, Boolean fromServer) {

        this.product = selectedProductObject;
        populateViewModel(selectedProductObject);


        // if product coming from server download images
        if (fromServer) {
            for (ProductImage prodImg : product.getImages()) {
                if (prodImg.getImage() != null && prodImg.getImage().getData() == null && prodImg.getImage().getImageAvailable() != null && prodImg.getImage().getImageAvailable()) {
                    downloadImage(prodImg);
                }
            }
        }
    }

    private void populateViewModel(Product selectedProductObject) {
        this.productID = selectedProductObject.getId();
        this.title = selectedProductObject.getTitle();
        if (!Utils.isEmptyOrNull(selectedProductObject.getDesc())) {
            this.desc = selectedProductObject.getDesc();
        }

        this.category = selectedProductObject.getCategory();
        this.isDeleted = selectedProductObject.getDeleted();
        this.isActive = selectedProductObject.getActive();
        this.isPublished = selectedProductObject.getPublished();
        this.price = selectedProductObject.getPrice() + "";


        // no Need NOman Does but android no need
//        if(product.getImages() != null && product.getImages().size() > 0){
//
//            this.images
//            self.images = product.images.compactMap({
//                    guard let imageData = $0.image.data else{
//                return UIImage(named: "bg_no_image")
//            }
//            return UIImage(data: imageData)
//            })
//            self.listOfImages = product.images.compactMap({$0})
//        }

    }


    public void addProduct(DatabaseTransactionInterface databaseTransactionInterface) {

        if (product == null) {
            product = new Product();
        }

        product.setTitle(title);
        product.setDesc(desc);
        product.setPrice(Utils.isEmptyOrNull(price) ? 0.0 : Double.parseDouble(price));
        product.setCategory(category);
        product.setActive(isActive);
        product.setPublished(isPublished);

        if (images != null && images.size() > 0) {
            for (int index = 0; index < images.size(); index++) {

                createImageDataObjectForDb(images.get(index), new DataSaveAndConvertInterface<Object, Image>() {
                    @Override
                    public void onSuccess(Object syncResponse, Image imgObject) {

                        ProductImage prodImage = new ProductImage();
                        prodImage.setProductID(product.getId());
                        prodImage.setImage(imgObject);
                        product.getImages().add(prodImage);
                    }

                    @Override
                    public void onError() {

                    }
                });

            }
        }

        AppDBHelper.getDBHelper().insertOrUpdateRecordToDB(product, databaseTransactionInterface);

        syncProducts();
    }


    public void syncProducts() {
        SyncManager.getInstance().addEntityToQueue(EntityEnum.Image);
        SyncManager.getInstance().addEntityToQueue(EntityEnum.ProductCategory);
        SyncManager.getInstance().addEntityToQueue(EntityEnum.Product);
        SyncManager.getInstance().addEntityToQueue(EntityEnum.ProductImage);
        SyncManager.getInstance().addEntityToQueue(EntityEnum.ImageSyncUtility);
    }

    public void updateProduct(DatabaseTransactionInterface databaseTransactionInterface) {

        try {

            AppDBHelper.getRealmInstance().beginTransaction();

            this.product.setTitle(title);
            this.product.setDesc(desc);
            this.product.setPrice(Utils.isEmptyOrNull(price) ? 0.0 : Double.parseDouble(price));
            this.product.setCategory(category);
            this.product.setActive(isActive);
            this.product.setPublished(isPublished);
            this.product.setLastModifiedDate(new Date());
            this.product.setDirty(true);
            this.product.setDeleted(isDeleted);


            if (images != null && images.size() > 0) {
                for (int index = 0; index < images.size(); index++) {

                    createImageDataObjectForDb(images.get(index), new DataSaveAndConvertInterface<Object, Image>() {
                        @Override
                        public void onSuccess(Object syncResponse, Image imgObject) {

                            ProductImage prodImage = new ProductImage();
                            prodImage.setProductID(product.getId());
                            prodImage.setImage(imgObject);
                            product.getImages().add(prodImage);
                        }

                        @Override
                        public void onError() {

                        }
                    });

                }
            }

            if (listOfDeletedImages != null && listOfDeletedImages.size() > 0) {

                for (int index = 0; index < listOfDeletedImages.size(); index++) {

                    ProductImage deletedProductImage = listOfDeletedImages.get(index);

                    //Image Remove Mark from the table "Image"
                    deletedProductImage.getImage().setData(null);
                    deletedProductImage.getImage().setDeleted(true);
                    deletedProductImage.getImage().setDirty(true);
                    deletedProductImage.getImage().setLastModifiedDate(new Date());

                    //Product Image Remove Mark from the table "ProductImage"
                    deletedProductImage.setDirty(true);
                    deletedProductImage.setDeleted(true);
                    deletedProductImage.setLastModifiedDate(new Date());
                }
            }


            //If product delete all images will be marked as remove
            if (isDeleted) {

                if (product.getImages() != null && product.getImages().size() > 0) {
                    for (ProductImage prodImage : product.getImages()) {

                        prodImage.getImage().setDirty(true);
                        prodImage.getImage().setDeleted(true);
                        prodImage.getImage().setLastModifiedDate(new Date());
                        prodImage.getImage().setData(null);

                        prodImage.setDirty(true);
                        prodImage.setDeleted(true);
                        prodImage.setLastModifiedDate(new Date());
                    }
                }
            }

            this.product.setImages(product.getImages());


            AppDBHelper.getRealmInstance().insertOrUpdate(product);

            AppDBHelper.getRealmInstance().commitTransaction();

            databaseTransactionInterface.onSuccessTransaction();

        } catch (Exception e) {

            if (AppDBHelper.getRealmInstance().isInTransaction()) {
                AppDBHelper.getRealmInstance().cancelTransaction();
            }

            e.printStackTrace();
            databaseTransactionInterface.onErrorTransaction();
        }

        //TODO: FOR SYNC
        syncProducts();

    }

    public void setProductCategory(ProductCategory parentCategory) {
        this.category = parentCategory;
    }

    public void deleteProductImgFromProductImgList(ProductImage productImage, DatabaseTransactionInterface databaseTransactionInterface) {
        try {

            AppDBHelper.getRealmInstance().beginTransaction();

            for (int index = 0; index < this.product.getImages().size(); index++) {

                if (productImage.getId().equalsIgnoreCase(this.product.getImages().get(index).getId())) {
                    this.product.getImages().remove(index);
                    break;
                }
            }
            this.product.setImages(this.product.getImages());

            AppDBHelper.getRealmInstance().commitTransaction();

            databaseTransactionInterface.onSuccessTransaction();
        } catch (Exception e) {

            if (AppDBHelper.getRealmInstance().isInTransaction()) {
                AppDBHelper.getRealmInstance().cancelTransaction();
            }

            e.printStackTrace();
            databaseTransactionInterface.onErrorTransaction();
        }
    }


    public void addProductToOrder(int quantity, String companyID, DataSaveAndConvertInterface<Object, Order> dataSaveAndConvertInterface) {


        boolean isError = false;
        final Order[] order = {null};

        RealmResults<Order> orders = null;
        orders = AppDBHelper.getRealmInstance().where(Order.class).equalTo("supplierCompanyID", companyID).findAll();


        if (orders != null && orders.size() > 0) {

            String[] ordersId = new String[orders.size()];

            for (int i = 0; i < orders.size(); i++) {
                ordersId[i] = orders.get(i).getId();
            }

            RealmResults<OrderStatus> orderStatuses = AppDBHelper.getRealmInstance().where(OrderStatus.class).in("order.id", ordersId).findAll();

            //Not Submitted
            OrderStatus notSubmittedOrders = orderStatuses.where().isNull("submitted").findFirst();
            if (notSubmittedOrders != null) {
                order[0] = notSubmittedOrders.getOrder();
            } else {

                RealmResults<OrderStatus> submittedOrders = orderStatuses.where().isNotNull("submitted").findAll();

                if (submittedOrders != null && submittedOrders.size() > 0) {

                    OrderStatus notApprovedOrders = submittedOrders.where().isNull("isApproved").findFirst();
                    if (notApprovedOrders != null) {
                        isError = true;
                        dataSaveAndConvertInterface.onError();
                        return;
                    }
                }

            }

        }


        if (!isError) {

            AppDBHelper.getRealmInstance().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    if (order[0] == null) {
                        order[0] = new Order();
                        order[0].setInitiationDate(new Date());
                        order[0].setInitiatingCompanyID(BizLinkedApplicationClass.getPreference().getProfileId());
                        order[0].setCustomerCompanyID(BizLinkedApplicationClass.getPreference().getProfileId());
                        order[0].setSupplierCompanyID(companyID);
                        OrderDetail detail = new OrderDetail();
                        detail.setOrderId(order[0].getId());
                        detail.setProductId(getProduct().getId());
                        detail.setQuantity(quantity);
                        detail.setPrice(getProduct().getPrice());
                        detail.setProductName(getProduct().getTitle());

                        if (getProduct().getCategory() != null) {
                            detail.setProductCategoryName(getProduct().getCategory().getTitle());
                            detail.setProductCategoryId(getProduct().getCategory().getId());
                        }

                        if (getProduct().getImages() != null && getProduct().getImages().size() > 0 && getProduct().getImages().first() != null && getProduct().getImages().first().getImage() != null) {
                            detail.setProductImageId(getProduct().getImages().first().getImage().getId());
                        }


                        if (order[0].getOrderDetails() == null) {
                            order[0].setOrderDetails(new RealmList<>());
                            ;
                        }
                        order[0].getOrderDetails().add(detail);

                        OrderStatus status = new OrderStatus();
                        status.setOrder(order[0]);

                        //Add in DB
                        realm.insertOrUpdate(order[0]);
                        realm.insertOrUpdate(status);

                    } else {

                        OrderDetail previousDetail = order[0].getOrderDetails().where().equalTo("productId", getProduct().getId()).findFirst();

                        if (previousDetail != null) {
                            previousDetail.setQuantity(quantity);
                            previousDetail.setLastModifiedDate(new Date());
                            ;
                            previousDetail.setDirty(true);
                        } else {

                            OrderDetail detail = new OrderDetail();
                            detail.setOrderId(order[0].getId());
                            ;
                            detail.setProductId(getProduct().getId());
                            detail.setQuantity(quantity);
                            detail.setPrice(getProduct().getPrice());
                            detail.setProductName(getProduct().getTitle());


                            if (getProduct().getCategory() != null) {
                                detail.setProductCategoryName(getProduct().getCategory().getTitle());
                                detail.setProductCategoryId(getProduct().getCategory().getId());
                            }

                            if (getProduct().getImages() != null && getProduct().getImages().size() > 0 && getProduct().getImages().first() != null && getProduct().getImages().first().getImage() != null) {
                                detail.setProductImageId(getProduct().getImages().first().getImage().getId());
                            }

                            order[0].getOrderDetails().add(detail);
                        }

                        order[0].setLastModifiedDate(new Date());
                        order[0].setDirty(true);
                    }

                    //on Success after all
                    dataSaveAndConvertInterface.onSuccess(null, order[0]);

                }
            });
        } else {
            dataSaveAndConvertInterface.onError();
        }
    }
}
