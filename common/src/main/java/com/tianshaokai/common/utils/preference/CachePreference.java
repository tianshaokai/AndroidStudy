package com.tianshaokai.common.utils.preference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.tianshaokai.common.utils.LogUtil;
import com.tianshaokai.common.utils.executor.ExecutorsManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class CachePreference {

    private static final ExecutorService WORKER = ExecutorsManager.getInstance().getNewPreferenceThreadPool();
    private final Map<String, Object> memoryCache = new ConcurrentHashMap<>();
    private final Map<String, JSONObject> memoryCacheJson = new ConcurrentHashMap<>();
    private final SharedPreferences preferences;

    public CachePreference(Context paramContext, String paramString) {
        this.preferences = paramContext.getSharedPreferences(paramString, Context.MODE_PRIVATE);
    }

    private void asyncPut(String paramString, Object paramObject) {
        WORKER.execute(new PutRunnable(paramString, paramObject));
    }

    public void putString(String paramKey, String paramValue) {
        memoryCache.put(paramKey, paramValue);
        asyncPut(paramKey, paramValue);
    }

    public String getString(String paramKey, String paramValue) {
        Object localObject = this.memoryCache.get(paramKey);
        if (localObject != null) {
            if ((localObject instanceof String)) {
                return ((String) localObject);
            }
            return paramValue;
        } else {
            if (this.preferences.contains(paramKey)) {
                paramValue = this.preferences.getString(paramKey, paramValue);
                if (paramValue == null) {
                    paramValue = "";
                }
                this.memoryCache.put(paramKey, paramValue);
                return paramValue;
            }
        }
        return paramValue;
    }

    public void putInt(String paramKey, int paramValue) {
        memoryCache.put(paramKey, paramValue);
        asyncPut(paramKey, paramValue);
    }

    public int getInt(String paramKey, int paramInt) {
        Object localObject = this.memoryCache.get(paramKey);
        if (localObject != null) {
            if ((localObject instanceof Integer)) {
                return ((Integer) localObject);
            }
            return paramInt;
        } else {
            if (this.preferences.contains(paramKey)) {
                paramInt = this.preferences.getInt(paramKey, paramInt);
                this.memoryCache.put(paramKey, paramInt);
                return paramInt;
            }
        }
        return paramInt;
    }

    public void putLong(String paramKey, long paramValue) {
        memoryCache.put(paramKey, paramValue);
        asyncPut(paramKey, paramValue);
    }

    public long getLong(String paramKey, long paramLong) {
        Object localObject = this.memoryCache.get(paramKey);
        if (localObject != null) {
            if ((localObject instanceof Long)) {
                return ((Long) localObject);
            }
            return paramLong;
        } else {
            if (this.preferences.contains(paramKey)) {
                paramLong = this.preferences.getLong(paramKey, paramLong);
                this.memoryCache.put(paramKey, paramLong);
                return paramLong;
            }
        }
        return paramLong;
    }

    public void putDouble(String paramKey, double paramValue) {
        memoryCache.put(paramKey, paramValue);
        asyncPut(paramKey, paramValue);
    }

    public double getDouble(String paramKey, double paramDouble) {
        Object localObject = this.memoryCache.get(paramKey);
        if (localObject != null) {
            if ((localObject instanceof Double)) {
                return ((Double) localObject);
            }
            return paramDouble;
        } else {
            if (this.preferences.contains(paramKey)) {
                paramDouble = Double.longBitsToDouble(preferences.getLong(paramKey,
                        Double.doubleToRawLongBits(paramDouble)));
                this.memoryCache.put(paramKey, paramDouble);
                return paramDouble;
            }
        }
        return paramDouble;
    }

    public void putFloat(String paramKey, float paramValue) {
        memoryCache.put(paramKey, paramValue);
        asyncPut(paramKey, paramValue);
    }

    public float getFloat(String paramKey, float paramFloat) {
        Object localObject = this.memoryCache.get(paramKey);
        if (localObject != null) {
            if ((localObject instanceof Float)) {
                return ((Float) localObject);
            }
            return paramFloat;
        } else {
            if (this.preferences.contains(paramKey)) {
                paramFloat = this.preferences.getFloat(paramKey, paramFloat);
                this.memoryCache.put(paramKey, paramFloat);
                return paramFloat;
            }
        }
        return paramFloat;
    }

    public void putBoolean(String paramKey, boolean paramValue) {
        memoryCache.put(paramKey, paramValue);
        asyncPut(paramKey, paramValue);
    }

    public boolean getBoolean(String paramKey, boolean paramBoolean) {
        Object localObject = this.memoryCache.get(paramKey);
        if (localObject != null) {
            if ((localObject instanceof Boolean)) {
                return ((Boolean) localObject);
            }
            return paramBoolean;
        } else {
            if (this.preferences.contains(paramKey)) {
                paramBoolean = this.preferences.getBoolean(paramKey, paramBoolean);
                this.memoryCache.put(paramKey, paramBoolean);
                return paramBoolean;
            }
        }
        return paramBoolean;
    }

    public void putJSON(String paramKey, JSONObject jsonObject) {
        memoryCacheJson.put(paramKey, jsonObject);
        asyncPut(paramKey, jsonObject.toString());
    }


    public JSONObject getJson(String str) {

        if(TextUtils.isEmpty(str)) return null;

        if (this.memoryCacheJson.containsKey(str)) {
            return this.memoryCacheJson.get(str);
        }

        String string = getString(str, "");
        if(TextUtils.isEmpty(string)) return null;

        try {

            JSONObject jsonObject = new JSONObject(string);
            this.memoryCacheJson.put(str, jsonObject);
            return jsonObject;
        } catch (JSONException e) {
            return null;
        }
    }


    public Map<String, ?> getAll() {
        return preferences.getAll();
    }

    public void clearAll() {

    }


    private class PutRunnable implements Runnable {
        private String key;
        private Object value;

        public PutRunnable(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        @SuppressLint("ApplySharedPref")
        @Override
        public void run() {
            SharedPreferences.Editor localEditor = preferences.edit();
            Object obj = this.value;
            if ((obj instanceof String)) {
                localEditor.putString(key, (String) obj);
            } else if ((obj instanceof Long)) {
                localEditor.putLong(key, (Long) obj);
            } else if ((obj instanceof Integer)) {
                localEditor.putInt(key, ((Integer) obj));
            } else if ((obj instanceof Boolean)) {
                localEditor.putBoolean(key, ((Boolean) obj));
            } else if ((obj instanceof Float)) {
                localEditor.putFloat(key, ((Float) obj));
            } else if ((obj instanceof Double)) {
                localEditor.putLong(key, Double.doubleToRawLongBits((Double) obj));
            } else {
                LogUtil.e("数据异常，无法存入sp");
            }
            localEditor.commit();
        }
    }
}
