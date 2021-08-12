package com.app.bizlinked.helpers.sync_helpers;

import android.widget.Toast;

import com.app.bizlinked.BizLinkedApplicationClass;
import com.app.bizlinked.activities.baseClass.BaseActivity;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.db.AppDBHelper;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.helpers.realm.FindDBHelper;
import com.app.bizlinked.helpers.ui.dialogs.DateFormatHelper;
import com.app.bizlinked.listener.custom.Convertable;
import com.app.bizlinked.listener.custom.DatabaseTransactionInterface;
import com.app.bizlinked.listener.custom.OnSyncCompleteInterafce;
import com.app.bizlinked.models.db.BusinessCategory;
import com.app.bizlinked.models.db.City;
import com.app.bizlinked.models.db.CompanyPackage;
import com.app.bizlinked.models.db.Image;
import com.app.bizlinked.models.db.Link;
import com.app.bizlinked.models.db.Order;
import com.app.bizlinked.models.db.OrderStatus;
import com.app.bizlinked.models.db.Product;
import com.app.bizlinked.models.db.ProductCategory;
import com.app.bizlinked.models.db.ProductImage;
import com.app.bizlinked.models.db.Profile;
import com.app.bizlinked.models.db.SyncQueue;
import com.app.bizlinked.models.db.SyncedEntity;
import com.app.bizlinked.models.db.base_class.IBaseEntity;
import com.app.bizlinked.models.enums.EntityEnum;
import com.app.bizlinked.models.sync_response.BaseSyncResponse;
import com.app.bizlinked.models.sync_response.BusinessCategorySyncResponse;
import com.app.bizlinked.models.sync_response.CategorySyncResponse;
import com.app.bizlinked.models.sync_response.CitySyncResponse;
import com.app.bizlinked.models.sync_response.CompanyPackageSyncResponse;
import com.app.bizlinked.models.sync_response.ImageSyncResponse;
import com.app.bizlinked.models.sync_response.LinkSyncResponse;
import com.app.bizlinked.models.sync_response.OrderStatusSyncResponse;
import com.app.bizlinked.models.sync_response.OrderSyncResponse;
import com.app.bizlinked.models.sync_response.ProductImageSyncResponse;
import com.app.bizlinked.models.sync_response.ProductSyncResponse;
import com.app.bizlinked.models.sync_response.ProfileSyncResponse;
import com.app.bizlinked.models.sync_response.SyncResponse;
import com.app.bizlinked.webhelpers.WebApiRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;

import io.reactivex.annotations.NonNull;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class SyncManager implements OnSyncCompleteInterafce{


    public RealmResults<SyncQueue> syncQueue;
    private boolean shouldSync = false;
    private boolean isFirstTime = true;
    private BaseActivity activityReference = null;

    private static SyncManager instance = new SyncManager();


    private SyncManager(){}
    public static SyncManager getInstance(){
        return instance;
    }

    public void setActivityReference(BaseActivity activityReference) {
        this.activityReference = activityReference;
    }

    public boolean isFirstTime() {
        return isFirstTime;
    }

    public void setFirstTime(boolean firstTime) {
        isFirstTime = firstTime;
    }

    public void init(){

        this.syncQueue = AppDBHelper.getRealmInstance().where(SyncQueue.class).sort("priority").findAll();

        this.syncQueue.addChangeListener(new RealmChangeListener<RealmResults<SyncQueue>>() {
            @Override
            public void onChange(RealmResults<SyncQueue> syncQueues) {
                //Utils.showToast(activityReference, "syncQueues size " + syncQueues.size(), AppConstant.TOAST_TYPES.INFO);
                triggredRealmListener(syncQueues);
            }
        });

        if(isFirstTime){
            isFirstTime = false;
            this.shouldSync = true;
            triggredRealmListener(this.syncQueue);
        }

    }

    private void triggredRealmListener(RealmResults<SyncQueue> syncQueues) {
        if(syncQueues != null && syncQueues.size() > 0){

            if(syncQueues.get(0) != null){ // safety check

                if(shouldSync){
                    shouldSync = false;
                    syncEntity(syncQueues.get(0), SyncManager.this);
                }
            }else {
                shouldSync = true;
                return;
            }
        }else {
            // First Time and Every time when Queue is Empty
            shouldSync = true;
            return;
        }
    }

    public void addEntityToQueue(EntityEnum entityEnums){

        if(FindDBHelper.byPrimaryKey(AppDBHelper.getRealmInstance(), SyncQueue.class, entityEnums.getEntityName()) == null){

            SyncQueue syncQueue = new SyncQueue();
            syncQueue.setEntityName(entityEnums.getEntityName());
            syncQueue.setPriority(entityEnums.getPriority());

            AppDBHelper.getRealmInstance().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.insert(syncQueue);
                }
            });
        }
    }


    // Sync entity based on Enums
    public void syncEntity(SyncQueue queue, OnSyncCompleteInterafce onSyncCompleteInterafce){


        if(queue.getEntityName().equalsIgnoreCase(EntityEnum.Product.getEntityName())){
            //Call function
            sync(queue, Product.class, ProductSyncResponse.class,AppConstant.ServerAPICalls.productSyncURL, onSyncCompleteInterafce);
        }else if(queue.getEntityName().equalsIgnoreCase(EntityEnum.ProductCategory.getEntityName())) {
            //Call function
            sync(queue, ProductCategory.class, CategorySyncResponse.class, AppConstant.ServerAPICalls.categorySyncURL, onSyncCompleteInterafce);
        }else if(queue.getEntityName().equalsIgnoreCase(EntityEnum.ProductImage.getEntityName())) {
            //Call function
            sync(queue, ProductImage.class, ProductImageSyncResponse.class, AppConstant.ServerAPICalls.productImageSyncURL, onSyncCompleteInterafce);
        }else if(queue.getEntityName().equalsIgnoreCase(EntityEnum.BusinessCategory.getEntityName())) {
            //Call function
            sync(queue, BusinessCategory.class, BusinessCategorySyncResponse.class, AppConstant.ServerAPICalls.businessCategorySyncURL, onSyncCompleteInterafce);
        }else if(queue.getEntityName().equalsIgnoreCase(EntityEnum.City.getEntityName())) {
            //Call function
            sync(queue, City.class, CitySyncResponse.class,AppConstant.ServerAPICalls.citySyncURL, onSyncCompleteInterafce);
        }else if(queue.getEntityName().equalsIgnoreCase(EntityEnum.Profile.getEntityName())) {
            //Call function
            sync(queue, Profile.class, ProfileSyncResponse.class, AppConstant.ServerAPICalls.profileSyncURL, onSyncCompleteInterafce);
        }else if(queue.getEntityName().equalsIgnoreCase(EntityEnum.Link.getEntityName())) {
            //Call function
            sync(queue, Link.class, LinkSyncResponse.class, AppConstant.ServerAPICalls.linkSyncURL, onSyncCompleteInterafce);
        }else if(queue.getEntityName().equalsIgnoreCase(EntityEnum.CompanyPackage.getEntityName())) {
            //Call function
            sync(queue, CompanyPackage.class, CompanyPackageSyncResponse.class, AppConstant.ServerAPICalls.companyPackageSyncURL, onSyncCompleteInterafce);
        }else if(queue.getEntityName().equalsIgnoreCase(EntityEnum.Order.getEntityName())) {
            //Call function
            sync(queue, Order.class, OrderSyncResponse.class, AppConstant.ServerAPICalls.orderSyncURL, onSyncCompleteInterafce);
        }else if(queue.getEntityName().equalsIgnoreCase(EntityEnum.OrderStatus.getEntityName())) {
            //Call function
            sync(queue, OrderStatus.class, OrderStatusSyncResponse.class, AppConstant.ServerAPICalls.orderStatusSyncURL, onSyncCompleteInterafce);
        }else if(queue.getEntityName().equalsIgnoreCase(EntityEnum.Image.getEntityName())) {
            //Call function
            sync(queue, Image.class, ImageSyncResponse.class,AppConstant.ServerAPICalls.imageSyncURL, onSyncCompleteInterafce);
        }else if(queue.getEntityName().equalsIgnoreCase(EntityEnum.ImageSyncUtility.getEntityName())) {
            ImageSyncUtility.getInstance().syncImagesData();
        }
    }


    private <T extends BaseSyncResponse, S extends RealmObject & IBaseEntity & Convertable<T>> void sync(SyncQueue queue, Class<S> realmObject, Class<T> syncClass, String url, OnSyncCompleteInterafce onCompletionallback){

        final String[] queueNameTobeSynced = {null};
        RealmResults<S> productResults = AppDBHelper.getRealmInstance()
                                                    .where(realmObject)
                                                    .equalTo("baseEntity.isDirty", true)
                                                    .findAll();


        ArrayList<S> products = new ArrayList<S>();
        if(productResults != null && productResults.size() > 0){
            products.addAll(productResults);
        }


        HashMap<String, Object> params = new HashMap<>();
        JsonArray deltas = new JsonArray();
        final SyncedEntity[] entity = {FindDBHelper.byPrimaryKey(AppDBHelper.getRealmInstance(), SyncedEntity.class, queue.getEntityName())};

//        //safety check
//        if(productResults == null){
//            return;
//        }

        for (S product: products) {
            deltas.add(product.encode());
        }

        params.put("delta", deltas);

        if(entity[0] != null && entity[0].getLastSyncDate() != null){
            params.put("lastSyncDate", DateFormatHelper.convertDateToServerFormattedString(entity[0].getLastSyncDate()));
        }else{
            params.put("lastSyncDate", null);
        }


        WebApiRequest.getInstance(activityReference, false).syncRequestToServer(url, params, new WebApiRequest.APIRequestDataCallBack() {
            @Override
            public void onSuccess(JsonElement response) {

                if (response != null && response.isJsonObject() && !response.isJsonNull()) {

                    //Json Object
                    JsonObject responseObject = response.getAsJsonObject();

                    //Conversion
                    Gson gson = new Gson();
                    SyncResponse<T> syncResponse = gson.fromJson(responseObject, new TypeToken<SyncResponse<T>>(){}.getType());

                    syncResponse.getDelta().clear();

                    for (JsonElement deltaJSONObjectResponse : responseObject.get("delta").getAsJsonArray())
                        syncResponse.getDelta().add(gson.fromJson(deltaJSONObjectResponse, syncClass));


                    if(syncResponse.getDelta() != null && syncResponse.getLastSyncDate() != null){

                        // First Step

                        for (S product: products) {
                            AppDBHelper.getRealmInstance().executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    product.setDirty(false);
                                }
                            });
                        }

                        // 2nd Step

                        for (T product: syncResponse.getDelta()) {

                            S savedProduct = FindDBHelper.byPrimaryKey(AppDBHelper.getRealmInstance(), realmObject, ((T) product).getId());

                            if(savedProduct != null){
                                Date lastModifiedLocal = DateFormatHelper.convertStringToServerFormattedDate(((T) product).getLastModified());
                                if (lastModifiedLocal != null && lastModifiedLocal.after(savedProduct.getLastModifiedDate())) {
                                    AppDBHelper.getRealmInstance().executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            savedProduct.decode(product);
                                        }
                                    });
                                }
                            }else{


                                AppDBHelper.getRealmInstance().executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {

                                        S prod = AppDBHelper.getRealmInstance().createObject(realmObject, ((T) product).getId());
//                                        prod.setId(((T) product).getId());
                                        prod.decode(((T) product));

                                        realm.insertOrUpdate(prod);
                                    }
                                });
                            }

                        }//end of For loop

                        //3rd Step

                        AppDBHelper.getRealmInstance().executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {

                                if(entity[0] == null){

//                                        if !queue.isInvalidated{ // agr transaction nahi horahi uska check hay ye
//                                    if (queue.isValid()) {
                                        entity[0] = new SyncedEntity();
                                        entity[0].setEntityName(queue.getEntityName());
                                        entity[0].setLastSyncDate(DateFormatHelper.convertStringToServerFormattedDate(syncResponse.getLastSyncDate()));

                                        realm.insertOrUpdate(entity[0]);
//                                    }

                                }else{

//                                    if(entity[0].isValid()){
                                        entity[0].setLastSyncDate(DateFormatHelper.convertStringToServerFormattedDate(syncResponse.getLastSyncDate()));
//                                    }
//                                        if let ent = entity, !ent.isInvalidated{ // agr transaction nahi horahi uska check hay ye
                                }

//                                if(queue.isValid()){
//                                    if !queue.isInvalidated, let queueObj = self.realm?.object(ofType: SyncQueue.self, forPrimaryKey: queue.entityName){
                                    SyncQueue queueObj = realm.where(SyncQueue.class).equalTo("entityName", queue.getEntityName()).findFirst();
//                                if (queueObj !=  null && queueObj.isValid()) {
                                    if (queueObj !=  null ) {
                                        queueNameTobeSynced[0] = queueObj.getEntityName();
                                        queueObj.deleteFromRealm();
                                    }
//                                }
                            }
                        });
                        //4th Step

                        resumeQueue();

                        if(onCompletionallback != null)
                          onCompletionallback.onSyncComplete(queueNameTobeSynced[0]);
                     }
                }else{
                    //On Error Case
                    AppDBHelper.getRealmInstance().executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
//                            if(queue.isValid()){
                                SyncQueue queueObj = FindDBHelper.byPrimaryKey(AppDBHelper.getRealmInstance(), SyncQueue.class, queue.getEntityName());
                                if(queueObj != null){
                                    queueObj.deleteFromRealm();
                                }
//                            }
                        }
                    });
                }
            }


            @Override
            public void onError(String response) {

                //on Error Case
                AppDBHelper.getRealmInstance().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
//                        if(queue.isValid()){
                            SyncQueue queueObj = FindDBHelper.byPrimaryKey(AppDBHelper.getRealmInstance(), SyncQueue.class, queue.getEntityName());
                            if(queueObj != null){
                                queueObj.deleteFromRealm();
                            }
//                        }
                    }
                });

            }

            @Override
            public void onNoNetwork() {

            }
        });
    }

    public void resumeQueue() {

        //safety check
        if(syncQueue != null && syncQueue.size() > 0){

//        where().notEqualTo("entityName", EntityEnum.ImageSyncUtility.getEntityName())
            SyncQueue queue = syncQueue.where().notEqualTo("entityName", EntityEnum.ImageSyncUtility.getEntityName()).findFirst();
            if(queue != null && !queue.getEntityName().equalsIgnoreCase(EntityEnum.ImageSyncUtility.getEntityName())){
                syncEntity(queue, SyncManager.this);
            }else{

                SyncQueue imageSyncQueue = syncQueue.where().findFirst();
                if(imageSyncQueue != null && imageSyncQueue.getEntityName().equalsIgnoreCase(EntityEnum.ImageSyncUtility.getEntityName())){
                    ImageSyncUtility.getInstance().setActivityReference(activityReference);
                    ImageSyncUtility.getInstance().syncImagesData();
                }
            }
        }
    }

    @Override
    public void onSyncComplete(String queueNameTobeSynced) {
//        Toast.makeText(activityReference, queueNameTobeSynced + " successfully synced...", Toast.LENGTH_LONG).show();
    }
}
