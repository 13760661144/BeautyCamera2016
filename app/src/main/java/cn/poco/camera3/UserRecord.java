package cn.poco.camera3;

import android.util.SparseArray;

/**
 * 用户记录
 * Created by Gxx on 2018/1/22.
 */

public class UserRecord
{
    private static SparseArray<UserRecord> mRecordArr;

    private SparseArray<Object> mRecordInfoArr;

    /**
     * 用户记录的类型
     */
    public interface RecordType
    {
        int CAMERA = 1;
    }

    /**
     * 记录的具体信息类型
     */
    public interface CameraRecordInfoType
    {
        int FRONT_AND_BACK_LENS = 1 << 10; // 记录前后置
        int CAPTURE_MODE = 1 << 11; // 记录定时拍照
    }

    public UserRecord()
    {
        mRecordInfoArr = new SparseArray<>();
    }

    public static void init()
    {
        if (mRecordArr == null)
        {
            mRecordArr = new SparseArray<>();
        }
    }

    public static void addRecord(int key, UserRecord record)
    {
        if (record != null && mRecordArr != null)
        {
            mRecordArr.put(key, record);
        }
    }

    public static UserRecord getRecord(int key)
    {
        return mRecordArr != null ? mRecordArr.get(key, null) : null;
    }

    public Object getRecordInfo(int type)
    {
       return mRecordInfoArr.get(type, null);
    }

    public void updateRecordInfo(int info_type, Object value)
    {
        if (mRecordInfoArr != null)
        {
            mRecordInfoArr.put(info_type, value);
        }
    }
}
