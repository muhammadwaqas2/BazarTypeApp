package com.app.bizlinked.helpers.sync_helpers;

import android.widget.Toast;

import com.app.bizlinked.activities.baseClass.BaseActivity;
import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.db.AppDBHelper;
import com.app.bizlinked.listener.custom.OnSyncCompleteInterafce;
import com.app.bizlinked.models.db.Image;
import com.app.bizlinked.models.db.SyncQueue;
import com.app.bizlinked.models.enums.EntityEnum;
import com.app.bizlinked.webhelpers.WebApiRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;

public class ImageSyncUtility implements OnSyncCompleteInterafce {

    private static ImageSyncUtility instance = new ImageSyncUtility();
    private BaseActivity activityReference = null;
    private ArrayList<String> downloadFailedImages = null;
    private ArrayList<String> uploadFailedImages = null;


    private ImageSyncUtility() {
    }

    public static ImageSyncUtility getInstance() {
        return instance;
    }

    public void setActivityReference(BaseActivity activityReference) {
        this.activityReference = activityReference;
    }


    public void syncImagesData() {

        downloadFailedImages = new ArrayList<>();
        uploadFailedImages = new ArrayList<>();

        downloadImages(ImageSyncUtility.this);
    }

    private void uploadImages(OnSyncCompleteInterafce onSyncCompleteInterafce) {

        RealmResults<Image> imagesToUpload = AppDBHelper.getRealmInstance()
                .where(Image.class)
                .equalTo("isUploaded", false)
                .equalTo("baseEntity.isDeleted", false)
                .not().in("id", uploadFailedImages.toArray(new String[0]))
                .findAll();

        Image image = null;

        if (imagesToUpload != null && imagesToUpload.size() > 0) {
            image = imagesToUpload.first();
        } else {
            AppDBHelper.getRealmInstance().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    SyncQueue queue = SyncManager.getInstance().syncQueue.where().equalTo("entityName", EntityEnum.ImageSyncUtility.getEntityName()).findFirst();
                    if (queue != null && queue.getEntityName().equalsIgnoreCase(EntityEnum.ImageSyncUtility.getEntityName())) {
                        queue.deleteFromRealm();
                    }
                }
            });

            return;

        }

        String imageUploadUrl = AppConstant.ServerAPICalls.imageUploadURL + (image.getId());

        Image finalImage = image;

        if(image.getData() != null && image.getData().length > 0){
            WebApiRequest.getInstance(activityReference, false).uploadImageRequest(imageUploadUrl, image.getData(), new WebApiRequest.ImageRequestDataCallBack() {
                @Override
                public void onSuccess(ResponseBody response) {

                    if (response != null) {

                        AppDBHelper.getRealmInstance().executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                finalImage.setUploaded(true);

                            }
                        });

                        //Listener
                        onSyncCompleteInterafce.onSyncComplete("ImageSyncUpload");
                        uploadQueueResume();
                    } else {
                        uploadFailedImages.add(finalImage.getId());
                        uploadQueueResume();
                    }

                }

                @Override
                public void onError(String response) {
                    uploadFailedImages.add(finalImage.getId());
                    uploadQueueResume();
                }

                @Override
                public void onNoNetwork() {

                }
            });

        }

    }


    private void uploadQueueResume() {
        SyncQueue syncQueueOtherThanImageSync = SyncManager.getInstance().syncQueue.where().notEqualTo("entityName", EntityEnum.ImageSyncUtility.getEntityName()).findFirst();
        if (syncQueueOtherThanImageSync != null && SyncManager.getInstance().syncQueue.contains(syncQueueOtherThanImageSync)) {
            SyncManager.getInstance().resumeQueue();
        } else {
            uploadImages(ImageSyncUtility.this);
        }
    }


    private void downloadImages(OnSyncCompleteInterafce onSyncCompleteInterafce) {


        RealmResults<Image> imagesToDownload = AppDBHelper.getRealmInstance()
                .where(Image.class)
                .isNull("data")
                .equalTo("isImageAvailable", true)
                .equalTo("baseEntity.isDeleted", false)
                .not().in("id", downloadFailedImages.toArray(new String[0]))
                .findAll();


        Image image = null;
        if (imagesToDownload != null && imagesToDownload.size() > 0) {
            image = imagesToDownload.first();
        } else {
            uploadImages(ImageSyncUtility.this);
            return;
        }


        String imageDownloadURL = AppConstant.ServerAPICalls.imageDownloadURL + (image.getId());

        Image finalImage = image;
        WebApiRequest.getInstance(activityReference, false).downloadImageRequest(imageDownloadURL, null, new WebApiRequest.ImageRequestDataCallBack() {
            @Override
            public void onSuccess(ResponseBody response) {

                try {
                    if (response != null && response.contentLength() > 0) {

//                        JsonObject responseObject = response.getAsJsonObject();

                        byte[] imageData = response.bytes();

                        AppDBHelper.getRealmInstance().executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                finalImage.setData(imageData);
                            }
                        });

                        //Listener
                        onSyncCompleteInterafce.onSyncComplete("ImageSyncDownload");
                        downloadQueueResume();
                    } else {
                        downloadFailedImages.add(finalImage.getId());
                        downloadQueueResume();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String response) {
                downloadFailedImages.add(finalImage.getId());
                downloadQueueResume();
            }

            @Override
            public void onNoNetwork() {

            }
        });
    }

    private void downloadQueueResume() {
        SyncQueue syncQueueOtherThanImageSync = SyncManager.getInstance().syncQueue.where().notEqualTo("entityName", EntityEnum.ImageSyncUtility.getEntityName()).findFirst();
        if (syncQueueOtherThanImageSync != null && SyncManager.getInstance().syncQueue.contains(syncQueueOtherThanImageSync)) {
            SyncManager.getInstance().resumeQueue();
        } else {
            downloadImages(ImageSyncUtility.this);
        }

    }


    public void downloadImageRequest(String url, HashMap<String, Object> params, final WebApiRequest.ImageRequestDataCallBack imageRequestDataCallBack) {
        WebApiRequest.getInstance(activityReference, false).downloadImageRequest(url, params, imageRequestDataCallBack);
    }

    @Override
    public void onSyncComplete(String queueNameTobeSynced) {
//        Toast.makeText(activityReference, queueNameTobeSynced + " successfully synced...", Toast.LENGTH_SHORT).show();
    }
}
