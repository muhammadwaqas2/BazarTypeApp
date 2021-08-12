package com.app.bizlinked.helpers.preference;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHelper {
	

	public void putStringPreference(Context context, String prefsName,
                                    String key, String value) {

		SharedPreferences preferences = context.getSharedPreferences(prefsName,
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();

		editor.putString(key, value);
		editor.apply();
	}

	protected String getStringPreference(Context context, String prefsName,
                                         String key) {

		SharedPreferences preferences = context.getSharedPreferences(prefsName,
				Activity.MODE_PRIVATE);
		return preferences.getString(key, "");
	}

	protected void putBooleanPreference(Context context, String prefsName,
                                        String key, boolean value) {

		SharedPreferences preferences = context.getSharedPreferences(prefsName,
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();

		editor.putBoolean(key, value);
		editor.apply();
	}

	protected boolean getBooleanPreference(Context context, String prefsName,
                                           String key) {

		SharedPreferences preferences = context.getSharedPreferences(prefsName,
				Activity.MODE_PRIVATE);
		return preferences.getBoolean(key, false);
	}

	protected void putIntegerPreference(Context context, String prefsName,
                                        String key, int value) {

		SharedPreferences preferences = context.getSharedPreferences(prefsName,
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();

		editor.putInt(key, value);
		editor.apply();
	}


	protected int getIntegerPreference(Context context, String prefsName,
                                       String key) {

		SharedPreferences preferences = context.getSharedPreferences(prefsName,
				Activity.MODE_PRIVATE);
		return preferences.getInt(key, -1);
	}

	protected void putLongPreference(Context context, String prefsName,
                                     String key, long value) {

		SharedPreferences preferences = context.getSharedPreferences(prefsName,
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();

		editor.putLong(key, value);
		editor.apply();
	}

	protected long getLongPreference(Context context, String prefsName,
                                     String key) {

		SharedPreferences preferences = context.getSharedPreferences(prefsName,
				Activity.MODE_PRIVATE);
		return preferences.getLong(key, Integer.MIN_VALUE);
	}

	protected void putFloatPreference(Context context, String prefsName,
                                      String key, float value) {

		SharedPreferences preferences = context.getSharedPreferences(prefsName,
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putFloat(key, value);
		editor.apply();
	}

	protected float getFloatPreference(Context context, String prefsName,
                                       String key) {

		SharedPreferences preferences = context.getSharedPreferences(prefsName,
				Activity.MODE_PRIVATE);
		return preferences.getFloat(key, Float.MIN_VALUE);
	}

	protected void removePreference(Context context, String prefsName,
                                    String key) {

		SharedPreferences preferences = context.getSharedPreferences(prefsName,
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();

		editor.remove(key);
		editor.apply();
	}



}
