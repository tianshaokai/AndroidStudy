package com.tianshaokai.common.utils.preference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.tianshaokai.common.utils.LogUtil;
import com.tianshaokai.common.utils.executor.ExecutorsManager;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class CachePreference {

    private static final ExecutorService WORKER = ExecutorsManager.getInstance().getNewPreferenceThreadPool();
    private final Map<String, Object> memoryCache = new ConcurrentHashMap<String, Object>();
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

    public void putInt(String paramKey, int paramValue) {
        memoryCache.put(paramKey, paramValue);
        asyncPut(paramKey, paramValue);
    }

    public void putLong(String paramKey, long paramValue) {
        memoryCache.put(paramKey, paramValue);
        asyncPut(paramKey, paramValue);
    }

    public void putDouble(String paramKey, double paramValue) {
        memoryCache.put(paramKey, paramValue);
        asyncPut(paramKey, paramValue);
    }

    public void putFloat(String paramKey, float paramValue) {
        memoryCache.put(paramKey, paramValue);
        asyncPut(paramKey, paramValue);
    }

    public void putBoolean(String paramKey, boolean paramValue) {
        memoryCache.put(paramKey, paramValue);
        asyncPut(paramKey, paramValue);
    }

    public void putJSON(JSONObject paramJSONObject) {
        Iterator<String> sIterator = paramJSONObject.keys();
        while (sIterator.hasNext()) {
            String key = sIterator.next();
            Object value = paramJSONObject.opt(key);
            memoryCache.put(key, value);
        }
        WORKER.execute(new PutRunnable(paramJSONObject));
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

    public Map<String, ?> getAll() {
        return preferences.getAll();
    }


    private class PutRunnable implements Runnable {
        private String key;
        private Object value;
        private JSONObject jsonObject;
        private boolean isJson = false;

        public PutRunnable(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        public PutRunnable(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
            this.isJson = true;
        }

        @SuppressLint("ApplySharedPref")
        @Override
        public void run() {
            SharedPreferences.Editor localEditor = preferences.edit();
            if (this.isJson) {
                Iterator<String> sIterator = jsonObject.keys();
                while (sIterator.hasNext()) {
                    String key = sIterator.next();
                    Object value = jsonObject.opt(key);
                    putValue(key, value, localEditor);
                }
            } else {
                putValue(this.key, this.value, localEditor);
            }
            localEditor.commit();
        }

        private void putValue(String key, Object value, SharedPreferences.Editor localEditor) {
            if ((value instanceof String)) {
                localEditor.putString(key, (String) value);
            } else if ((value instanceof Long)) {
                localEditor.putLong(key, (Long) value);
            } else if ((value instanceof Integer)) {
                localEditor.putInt(key, ((Integer) value));
            } else if ((value instanceof Boolean)) {
                localEditor.putBoolean(key, ((Boolean) value));
            } else if ((value instanceof Float)) {
                localEditor.putFloat(key, ((Float) value));
            } else if ((value instanceof Double)) {
                localEditor.putLong(key, Double.doubleToRawLongBits((Double) value));
            } else {
                LogUtil.e("数据异常，无法存入sp");
            }
        }
    }
}
