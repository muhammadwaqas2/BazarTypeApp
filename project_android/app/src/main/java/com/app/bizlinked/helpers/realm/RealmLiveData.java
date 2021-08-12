package com.app.bizlinked.helpers.realm;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import io.realm.RealmResults;

// This works for results of any RealmModel in your project
public class RealmLiveData<T extends RealmModel> extends LiveData<RealmResults<T>> {
    private RealmResults<T> results;
    private final RealmChangeListener<RealmResults<T>> listener =
            new RealmChangeListener<RealmResults<T>>() {
                @Override
                public void onChange(RealmResults<T> results) {
                    setValue(results);
                }
            };
    public RealmLiveData(RealmResults<T> realmResults) {
        results = realmResults;
    }

    @Override
    protected void onActive() {
        results.addChangeListener(listener);
    }

    @Override
    protected void onInactive() {
        results.removeChangeListener(listener);
    }
}
