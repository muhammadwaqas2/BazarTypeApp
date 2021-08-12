package com.app.bizlinked.listener.custom;

import android.os.Parcelable;

import com.google.gson.JsonElement;

public interface Convertable<T> {

    JsonElement encode();
    void decode(T decodeAbleClass);
}
