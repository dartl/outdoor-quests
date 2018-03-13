package ru.gawk.historygeocachingdemo.adapters;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by GAWK on 28.11.2017.
 */

public class PrefUtil {
    // константы названий настроек
    public static final String TEST_START_MESSAGE = "test_start_message";
    public static final String ACTIVE_QUEST = "active_quest";
    public static final String COMPLETE_QUEST = "complete_quest";

    // основной код
    public static final String PERSISTANT_STORAGE_NAME = "HISTORY_GEOCACHING";
    private SharedPreferences sharedPreferences;

    public PrefUtil(SharedPreferences _sp) {
        sharedPreferences = _sp;
    }

    public PrefUtil(Context context) {
        sharedPreferences = context.getSharedPreferences(PERSISTANT_STORAGE_NAME, Context.MODE_PRIVATE);
    }

    public String getString(String key, String def) {
        return sharedPreferences.getString(key,def);
    }

    public boolean saveString(String key, String value) {
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putString(key, value);
        return ed.commit();
    }

    public boolean saveInt(String key, int value) {
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putInt(key, value);
        return ed.commit();
    }

    public int getInt(String key, int def) {
        return sharedPreferences.getInt(key,def);
    }

    public boolean saveBoolean(String key, boolean value) {
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putBoolean(key, value);
        return ed.commit();
    }

    public boolean getBoolean(String key, boolean def) {
        return sharedPreferences.getBoolean(key,def);
    }

    public boolean saveLong(String key, long value) {
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putLong(key, value);
        return ed.commit();
    }

    public long getLong(String key, long def) {
        return sharedPreferences.getLong(key,def);
    }

    public boolean saveStringSet(String key, Set<String> set) {
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putStringSet(key,set);
        return ed.commit();
    }

    public Set<String> getStringSet(String key, Set<String> set) {
        return sharedPreferences.getStringSet(key, set);
    }
}
