package com.tianshaokai.common.utils.preference;

import android.content.Context;

import java.util.concurrent.ConcurrentHashMap;

public class Preferences extends CachePreference {

    private static final String SP_NAME = "Android_Common";

    private static final ConcurrentHashMap<String, Preferences> cache = new ConcurrentHashMap<>();

    private Preferences(Context paramContext, String paramString) {
        super(paramContext, paramString);
    }

    public static Preferences build(Context paramContext) {
        return build(paramContext, SP_NAME);
    }

    public static Preferences build(Context paramContext, String paramString) {
        if (cache.containsKey(paramString)) {
            return cache.get(paramString);
        }
        Preferences preferences = new Preferences(paramContext, paramString);
        cache.put(paramString, preferences);
        return preferences;
    }
}
