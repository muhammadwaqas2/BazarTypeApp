package com.app.bizlinked.listener.custom;


public interface DataSaveAndConvertInterface<E, T> {

    void onSuccess(E syncResponse, T realmObject);
    void onError();
}
