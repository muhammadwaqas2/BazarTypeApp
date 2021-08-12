package com.app.bizlinked.helpers.realm;

import com.app.bizlinked.db.AppDBHelper;
import com.app.bizlinked.listener.custom.DatabaseTransactionInterface;
import com.app.bizlinked.models.db.ProductCategory;
import com.app.bizlinked.models.db.Profile;

import io.realm.Realm;
import io.realm.RealmObject;

public class DBHelper<T extends RealmObject> {

    public void copyRecordToDB(T table, DatabaseTransactionInterface databaseTransactionInterface) {

        AppDBHelper.getRealmInstance().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(table);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                databaseTransactionInterface.onSuccessTransaction();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                databaseTransactionInterface.onErrorTransaction();
            }
        });
    }

    public void insertOrUpdateRecordToDB(T table, DatabaseTransactionInterface databaseTransactionInterface) {

        AppDBHelper.getRealmInstance().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(table);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                databaseTransactionInterface.onSuccessTransaction();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                databaseTransactionInterface.onErrorTransaction();
            }
        });
    }

    public void insertOrUpdateRecordToDBSync(T table, DatabaseTransactionInterface databaseTransactionInterface) {

        try {
            AppDBHelper.getRealmInstance().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    realm.insertOrUpdate(table);

                    databaseTransactionInterface.onSuccessTransaction();
                }
            });
        }catch (Exception e){
           e.printStackTrace();
           databaseTransactionInterface.onErrorTransaction();
        }
    }

    public void insertOrUpdateRecordToDBSync(T table) {

        try {
            AppDBHelper.getRealmInstance().beginTransaction();
            AppDBHelper.getRealmInstance().insertOrUpdate(table);
            AppDBHelper.getRealmInstance().commitTransaction();
        }catch (Exception e){

            if(AppDBHelper.getRealmInstance().isInTransaction()){
                AppDBHelper.getRealmInstance().cancelTransaction();
            }
        }
    }
}
