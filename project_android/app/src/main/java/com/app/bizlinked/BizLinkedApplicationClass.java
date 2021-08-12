package com.app.bizlinked;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.multidex.MultiDex;

import com.app.bizlinked.constant.AppConstant;
import com.app.bizlinked.helpers.common.Utils;
import com.app.bizlinked.helpers.preference.BasePreferenceHelper;
import com.app.bizlinked.helpers.preference.PreferenceHelper;
import com.app.bizlinked.helpers.realm.RealmMigrations;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.annotations.RealmModule;
import io.sentry.SentryLevel;
import io.sentry.android.core.SentryAndroid;

public class BizLinkedApplicationClass extends Application {


    //Preference
    static BasePreferenceHelper basePreferenceHelper;

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if(!BuildConfig.ENVIRONMENT.equalsIgnoreCase(AppConstant.ENVIRONMENT.DEBUG)){

            SentryAndroid.init(this, options -> {

                options.setEnvironment(BuildConfig.ENVIRONMENT);
                // Add a callback that will be used before the event is sent to Sentry.
                // With this callback, you can modify the event or, when returning null, also discard the event.
                options.setBeforeSend((event, hint) -> {

                    options.setEnvironment(BuildConfig.ENVIRONMENT);

                    if (SentryLevel.DEBUG.equals(event.getLevel()))
                        return null;
                    else
                        return event;
                });
            });
        }


        // Operations on Crashlytics.
        //Crashlytics.someAction();

        setupPreference();

        initializeRealmInstance();
        //Because of network calls
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
        //Fabric.with(this, new Crashlytics());


    }

    public void initializeRealmInstance() {
        Realm.init(BizLinkedApplicationClass.this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name(BuildConfig.DATABASE_NAME)
                .schemaVersion(BuildConfig.DATABASE_VERSION)
//                .migration(new RealmMigrations())
//                .migration(new RealmMigrations(Realm.getDefaultInstance().getSchema().getAll().iterator()))
                //.deleteRealmIfMigrationNeeded()
                .build();



        Realm.setDefaultConfiguration(realmConfig);

    }

    @Override
    public void onTerminate() {
        Realm.getDefaultInstance().close();
        super.onTerminate();
    }


    private void setupPreference() {
        basePreferenceHelper = new BasePreferenceHelper(getApplicationContext());
    }


    public static BasePreferenceHelper getPreference() {
        return basePreferenceHelper;
    }



    public void showToast(final String msg, final int type){

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {
                Utils.showToast(getApplicationContext(), msg, type);
            }
        });


    }

}
