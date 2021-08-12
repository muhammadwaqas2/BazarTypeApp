package com.app.bizlinked.helpers.realm;

import com.app.bizlinked.constant.AppConstant;

import java.util.Hashtable;

import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmObjectSchema;

public final class FindDBHelper {

//     shared cache for primary keys
    private static Hashtable<Class<? extends RealmModel>, String> primaryKeyMap = new Hashtable<>();

    private static String getPrimaryKeyName(Realm realm, Class<? extends RealmModel> clazz) {
        String primaryKey = primaryKeyMap.get(clazz);
        if (primaryKey != null)
            return primaryKey;
        RealmObjectSchema schema = realm.getSchema().get(clazz.getSimpleName());
        if (!schema.hasPrimaryKey())
            return null;
        primaryKey = schema.getPrimaryKey();
        primaryKeyMap.put(clazz, primaryKey);
        return primaryKey;
    }

    private static <E extends RealmModel, TKey> E findByKey(Realm realm, Class<E> clazz, TKey key) {
        String primaryKey = getPrimaryKeyName(realm, clazz);
        if (primaryKey == null)
            return null;
        if (key instanceof String)
            return realm.where(clazz).equalTo(primaryKey, (String)key).findFirst();
        else
            return realm.where(clazz).equalTo(primaryKey, (Long)key).findFirst();
    }

    public static <E extends RealmModel> E byPrimaryKey(Realm realm, Class<E> clazz, String key) {
        if(key == null)
            return null;
        return findByKey(realm, clazz, key);
    }

    public static <E extends RealmModel> E byPrimaryKey(Realm realm, Class<E> clazz, Long key) {
        return findByKey(realm, clazz, key);
    }

    public static <E extends RealmObject> E byKey(Realm realm, Class<E> clazz, String key, String value) {
        if(key == null)
            return null;
        return realm.where(clazz).equalTo(key, value).findFirst();
    }


//    public static <E extends RealmModel> E byPrimaryKey(Realm realm, Class<E> clazz, String val) {
//        return findByKey(realm, clazz, AppConstant.DB_HELPER.PRIMARY_KEY, val);
//    }
//
//    public static <E extends RealmModel> E byKey(Realm realm, Class<E> clazz, String key, String val) {
//        return findByKey(realm, clazz, key, val);
//    }
//
//    private static <E extends RealmModel, TKey, TVal> E findByKey(Realm realm, Class<E> clazz, TKey key, TVal val) {
//        if (key == null || val == null)
//            return null;
//        return realm.where(clazz).equalTo((String)key, (String)val).findFirst();
//
//    }


}
