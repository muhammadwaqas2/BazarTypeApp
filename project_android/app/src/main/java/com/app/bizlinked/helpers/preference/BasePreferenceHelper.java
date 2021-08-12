package com.app.bizlinked.helpers.preference;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.app.bizlinked.models.UserModal;
import com.app.bizlinked.models.db.Profile;
import com.google.gson.GsonBuilder;
//import com.google.gson.GsonBuilder;


public class BasePreferenceHelper extends PreferenceHelper {

    private Context context;
    protected static final String KEY_LOGIN_STATUS = "is_login";
    protected static final String KEY_USER = "user";
    protected static final String PROFILE_ID = "profile_id";
    public static final String KEY_DEVICE_TOKEN = "device_token";
    public static final String AUTHENTICATE_USER_TOKEN_TYPE = "user_token_type";
    public static final String AUTHENTICATE_USER_TOKEN = "user_token";
    public static final String AUTHENTICATE_USER_REFRESH_TOKEN = "user_refresh_token";
    private static final String FILENAME = "file_preferences";
    protected static final String KEY_DEVICE_SEND_STATUS = "is_device_info_send";

    protected static final String KEY_USER_LOCATION = "user_location";

    public BasePreferenceHelper(Context c) {
        this.context = c;
    }

    public SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(FILENAME, Activity.MODE_PRIVATE);
    }

    public void setLoginStatus(boolean isLogin) {
        putBooleanPreference(context, FILENAME, KEY_LOGIN_STATUS, isLogin);
    }


    public void setStringPrefrence(String key, String value) {
        putStringPreference(context, FILENAME, key, value);
    }

    public String getStringPrefrence(String key) {
        return getStringPreference(context, FILENAME, key);
    }


    public void setIntegerPrefrence(String key, int value) {
        putIntegerPreference(context, FILENAME, key, value);
    }

    public int getIntegerPrefrence(String key) {
        return getIntegerPreference(context, FILENAME, key);
    }


    public void setBooleanPrefrence(String Key, boolean value) {
        putBooleanPreference(context, FILENAME, Key, value);
    }

    public boolean getBooleanPrefrence(String Key) {
        return getBooleanPreference(context, FILENAME, Key);
    }


    public void setDeviceInfoSendStatus(boolean isDeviceInfoSentToServer) {
        putBooleanPreference(context, FILENAME, KEY_DEVICE_SEND_STATUS, isDeviceInfoSentToServer);
    }


    public boolean getDeviceInfoSendStatus() {
        return getBooleanPreference(context, FILENAME, KEY_DEVICE_SEND_STATUS);
    }

    public boolean getLoginStatus() {
        return getBooleanPreference(context, FILENAME, KEY_LOGIN_STATUS);
    }

    public void putDeviceToken(String token) {
        putStringPreference(context, FILENAME, KEY_DEVICE_TOKEN, token);
    }


    public String getDeviceToken() {
        return getStringPreference(context, FILENAME, KEY_DEVICE_TOKEN);
    }


    public void putUserRefreshToken(String token) {
        putStringPreference(context, FILENAME, AUTHENTICATE_USER_REFRESH_TOKEN, token);
    }


    public String getUserRefreshToken() {
        return getStringPreference(context, FILENAME, AUTHENTICATE_USER_REFRESH_TOKEN);
    }

    public void putUserToken(String token) {
        putStringPreference(context, FILENAME, AUTHENTICATE_USER_TOKEN, token);
    }

    public String getUserToken() {
        return getStringPreference(context, FILENAME, AUTHENTICATE_USER_TOKEN);
    }

    public void putUserTokenType(String tokenType) {
        putStringPreference(context, FILENAME, AUTHENTICATE_USER_TOKEN_TYPE, tokenType);
    }
    public String getUserTokenType() {
        return getStringPreference(context, FILENAME, AUTHENTICATE_USER_TOKEN_TYPE);
    }

    public void putProfileId(String id) {
        putStringPreference(context, FILENAME, PROFILE_ID, id);
    }
    public String getProfileId() {
        return getStringPreference(context, FILENAME, PROFILE_ID);
    }

    public void putUser(Profile user) {
        putStringPreference(context,
                FILENAME,
                KEY_USER,
                new GsonBuilder()
                        .create()
                        .toJson(user));
    }

    public Profile getUser() {
        return new GsonBuilder().create().fromJson(
                getStringPreference(context, FILENAME, KEY_USER), Profile.class);
    }


    public void putUserCurrentPosition(Location location) {
        putStringPreference(context,
                FILENAME,
                KEY_USER_LOCATION,
                new GsonBuilder()
                        .create()
                        .toJson(location));
    }

    public Location getUserCurrentPosition() {
        return new GsonBuilder().create().fromJson(
                getStringPreference(context, FILENAME, KEY_USER_LOCATION), Location.class);
    }

    public void removeLoginPreference() {
        setLoginStatus(false);
        removePreference(context, FILENAME, KEY_USER);
        removePreference(context, FILENAME, PROFILE_ID);
        removePreference(context, FILENAME, KEY_LOGIN_STATUS);
        removePreference(context, FILENAME, KEY_DEVICE_TOKEN);
        removePreference(context, FILENAME, AUTHENTICATE_USER_TOKEN);
        removePreference(context, FILENAME, AUTHENTICATE_USER_REFRESH_TOKEN);
        removePreference(context, FILENAME, AUTHENTICATE_USER_TOKEN_TYPE);
    }


}
