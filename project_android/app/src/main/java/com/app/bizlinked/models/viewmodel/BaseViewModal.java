package com.app.bizlinked.models.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.app.bizlinked.db.AppDBHelper;
import com.app.bizlinked.helpers.file.FileHelper;
import com.app.bizlinked.listener.custom.DatabaseTransactionInterface;
import com.app.bizlinked.listener.custom.DataSaveAndConvertInterface;
import com.app.bizlinked.models.db.Image;

import java.io.File;

public class BaseViewModal extends ViewModel {

    public void saveImageToDb(File imageFile, DataSaveAndConvertInterface callBack) {

        byte[] imageBytesFile = FileHelper.readBytesFromFile(imageFile);

        if (imageBytesFile != null && imageBytesFile.length > 0) {

            Log.d(ProfileViewModal.class.getName(), imageBytesFile.length + "");
            Image imgObject = new Image();
            imgObject.setData(imageBytesFile);

            AppDBHelper.getDBHelper().insertOrUpdateRecordToDB(imgObject, new DatabaseTransactionInterface() {
                @Override
                public void onSuccessTransaction() {
                    callBack.onSuccess(null, imgObject);
                }

                @Override
                public void onErrorTransaction() {
                    callBack.onError();
                }
            });

        }
    }

    public void createImageDataObjectForDb(File imageFile, DataSaveAndConvertInterface callBack){
        try {
            byte[] imageBytesFile = FileHelper.readBytesFromFile(imageFile);

            if (imageBytesFile != null && imageBytesFile.length > 0) {

                Log.d(BaseViewModal.class.getName(), imageBytesFile.length + "");
                Image imgObject = new Image();
                imgObject.setData(imageBytesFile);

                if(callBack != null)
                    callBack.onSuccess(null, imgObject);
            }
        }catch (Exception e){
            e.printStackTrace();
            callBack.onError();
        }

    }


    public void saveImageToDbSync(File imageFile, boolean isDBSAve, DataSaveAndConvertInterface callBack) {
        try {
            byte[] imageBytesFile = FileHelper.readBytesFromFile(imageFile);

            if (imageBytesFile != null && imageBytesFile.length > 0) {

                Log.d(BaseViewModal.class.getName(), imageBytesFile.length + "");
                Image imgObject = new Image();
                imgObject.setData(imageBytesFile);

                AppDBHelper.getDBHelper().insertOrUpdateRecordToDBSync(imgObject);
                callBack.onSuccess(null, imgObject);
            }
        }catch (Exception e){
            e.printStackTrace();
            callBack.onError();
        }


    }


}
