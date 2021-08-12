package com.app.bizlinked.db;

import com.app.bizlinked.helpers.realm.DBHelper;
import io.realm.Realm;


public class AppDBHelper{

    public static Realm getRealmInstance(){
        return Realm.getDefaultInstance();
    }

    private static DBHelper dbHelper = null;

    public static DBHelper getDBHelper(){

        if(dbHelper == null){
            dbHelper = new DBHelper();
        }
        return dbHelper;
    }

}
