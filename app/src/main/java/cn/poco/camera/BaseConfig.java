package cn.poco.camera;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zwq on 2017/02/27 16:52.<br/><br/>
 */
public abstract class BaseConfig {

    private HashMap<String, Object> mDefaultData;
    private HashMap<String, Object> mData;
    private boolean mDataIsChange;

    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    @Retention(RetentionPolicy.SOURCE)
    public @interface ValueType {
        int T_Invalid = -1;
        int T_Int = 0;
        int T_Long = 1;
        int T_Float = 2;
        int T_Boolean = 3;
        int T_String = 4;
    }

    public final void initAll(Context context) {
        if (!checkIsInit()) {
            mContext = context;
            initAllData();
        }
    }

    public final boolean checkIsInit() {
        if (mContext == null || mSharedPreferences == null || mDefaultData == null || mData == null) {
            return false;
        }
        return true;
    }

    private final void initAllData() {
        mSharedPreferences = getSharedPreferences(mContext);

        if (mDefaultData == null) {
            mDefaultData = new HashMap<String, Object>();
        } else {
            mDefaultData.clear();
        }
        initDefaultData(mDefaultData);

        if (mData == null) {
            mData = new HashMap<String, Object>();
        } else {
            mData.clear();
        }
        mData.putAll(mDefaultData);

        initCustomData();
    }

    public abstract SharedPreferences getSharedPreferences(Context context);

    public abstract void initDefaultData(HashMap<String, Object> defaultData);

    private void initCustomData() {
        if (mSharedPreferences == null) return;
        if (mData == null) return;
        for (Map.Entry<String, Object> entry : mData.entrySet()) {
            Object value = entry.getValue();
            if (checkValueType(value) == ValueType.T_Int) {
                entry.setValue(mSharedPreferences.getInt(entry.getKey(), getInt(entry.getKey(), true)));
            } else if (checkValueType(value) == ValueType.T_Long) {
                entry.setValue(mSharedPreferences.getLong(entry.getKey(), getLong(entry.getKey(), true)));
            } else if (checkValueType(value) == ValueType.T_Float) {
                entry.setValue(mSharedPreferences.getFloat(entry.getKey(), getFloat(entry.getKey(), true)));
            } else if (checkValueType(value) == ValueType.T_Boolean) {
                entry.setValue(mSharedPreferences.getBoolean(entry.getKey(), getBoolean(entry.getKey(), true)));
            } else if (checkValueType(value) == ValueType.T_String) {
                entry.setValue(mSharedPreferences.getString(entry.getKey(), getString(entry.getKey(), true)));
            }
        }
    }

    private boolean saveData(String key, Object value, boolean apply) {
        if (mSharedPreferences == null) {
            return false;
        }
        if (mEditor == null) {
            mEditor = mSharedPreferences.edit();
        }
        if (checkValueType(value) == ValueType.T_Int) {
            mEditor.putInt(key, (Integer) value);
        } else if (checkValueType(value) == ValueType.T_Long) {
            mEditor.putLong(key, (Long) value);
        } else if (checkValueType(value) == ValueType.T_Float) {
            mEditor.putFloat(key, (Float) value);
        } else if (checkValueType(value) == ValueType.T_Boolean) {
            mEditor.putBoolean(key, (Boolean) value);
        } else if (checkValueType(value) == ValueType.T_String) {
            mEditor.putString(key, value.toString());
        } else {
            return false;
        }
        if (apply) {
            mEditor.apply();
        }
        return true;
    }

    public boolean saveData(String key, Object value) {
        if (mData == null) return false;
        mData.put(key, value);
        return saveData(key, value, true);
    }

    public final void saveAllData() {
        if (mData == null) return;
        if (!mDataIsChange) return;
        int len = mData.size();
        int count = 0;
        for (Map.Entry<String, Object> entry : mData.entrySet()) {
            saveData(entry.getKey(), entry.getValue(), count == len - 1 ? true : false);
            count++;
        }
        mDataIsChange = false;
    }

    public final void putData(String key, Object value) {
        if (mData == null) return;
        mData.put(key, value);
        mDataIsChange = true;
    }

    public final void resetAllData() {
        if (mDefaultData != null && mData != null) {
            mData.clear();
            mData.putAll(mDefaultData);
            mDataIsChange = true;
            saveAllData();
        }
    }

    public void clearAll() {
        if (mDefaultData != null) {
            mDefaultData.clear();
            mDefaultData = null;
        }
        if (mData != null) {
            mData.clear();
            mData = null;
        }
        mSharedPreferences = null;
        if (mEditor != null) {
            mEditor.clear();
            mEditor = null;
        }
        mContext = null;
    }

    protected int checkValueType(Object value) {
        if (value != null) {
            if (value instanceof Integer) {
                return ValueType.T_Int;
            } else if (value instanceof Long) {
                return ValueType.T_Long;
            } else if (value instanceof Float) {
                return ValueType.T_Float;
            } else if (value instanceof Boolean) {
                return ValueType.T_Boolean;
            } else if (value instanceof String) {
                return ValueType.T_String;
            }
        }
        return ValueType.T_Invalid;
    }

    public Object getValue(String key, boolean isDefault) {
        Object value = null;
        if (isDefault) {
            if (mDefaultData != null) {
                value = mDefaultData.get(key);
            }
        } else {
            if (mData != null) {
                value = mData.get(key);
            }
        }
        return value;
    }

    public int getInt(String key) {
        return getInt(key, false);
    }

    public int getInt(String key, boolean isDefault) {
        Object value = getValue(key, isDefault);
        if (checkValueType(value) == ValueType.T_Int) {
            return ((Integer) value).intValue();
        }
        return 0;
    }

    public long getLong(String key) {
        return getLong(key, false);
    }

    public long getLong(String key, boolean isDefault) {
        Object value = getValue(key, isDefault);
        if (checkValueType(value) == ValueType.T_Long) {
            return ((Long) value).longValue();
        }
        return 0L;
    }

    public float getFloat(String key) {
        return getFloat(key, false);
    }

    public float getFloat(String key, boolean isDefault) {
        Object value = getValue(key, isDefault);
        if (checkValueType(value) == ValueType.T_Float) {
            return ((Float) value).floatValue();
        }
        return 0.0f;
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean isDefault) {
        Object value = getValue(key, isDefault);
        if (checkValueType(value) == ValueType.T_Boolean) {
            return ((Boolean) value).booleanValue();
        }
        return false;
    }

    public String getString(String key) {
        return getString(key, false);
    }

    public String getString(String key, boolean isDefault) {
        Object value = getValue(key, isDefault);
        if (checkValueType(value) == ValueType.T_String) {
            return value.toString();
        }
        return "";
    }

}
