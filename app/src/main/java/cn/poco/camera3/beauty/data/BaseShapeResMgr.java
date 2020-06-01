package cn.poco.camera3.beauty.data;

import android.content.Context;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.poco.tianutils.CommonUtils;

/**
 * @author lmx
 *         Created by lmx on 2018-01-16.
 */

public abstract class BaseShapeResMgr<ResInfoType extends BaseInfo, ResInfoArrType> implements ResInfoArr<ResInfoType, ResInfoArrType>
{
    //为了减少升级/降级带来的BUG,只有版本相同才读取数据
    public int CURRENT_RES_JSON_VERSION = 1;

    protected ResInfoArrType mSdcardResInfoArr;

    public final Object SDCARD_LOCK = new Object();

    public ResInfoArrType SyncGetSdcardArr(Context context)
    {
        synchronized (SDCARD_LOCK)
        {
            if (mSdcardResInfoArr == null || GetResInfoArrSize(mSdcardResInfoArr) == 0)
            {
                ResInfoArrType sdcardInfoRes = GetSdcardInfoRes(context);
                mSdcardResInfoArr = CheckSdcardInfoRes(context, sdcardInfoRes);
            }
        }
        return mSdcardResInfoArr;
    }

    public boolean SyncSaveSdcardArr(Context context, ResInfoArrType arr)
    {
        synchronized (SDCARD_LOCK) {
            return SaveSdcardRes(context, arr);
        }
    }


    /**
     * 初始化记载sdcard数据文件不存在，返回null，通过{@link #CheckSdcardInfoRes(Context, Object)}}做处理
     */
    protected ResInfoArrType GetSdcardInfoRes(Context context)
    {
        ResInfoArrType out = null;
        Object object = CommonUtils.ReadFile(GetSdcardPath(context));

        if (object == null)
        {
            return null;
        }
        else
        {
            out = DecodeResInfoArr(context, object);
        }

        //再次判null
        if (out == null) return null;
        return out;
    }

    protected ResInfoArrType DecodeResInfoArr(Context context, Object data)
    {
        ResInfoArrType out = null;
        try
        {
            if (data != null)
            {
                JSONObject json = new JSONObject(new String((byte[]) data));
                if (json.length() > 0)
                {
                    if (json.has("version"))
                    {
                        CURRENT_RES_JSON_VERSION = json.getInt("version");
                    }
                    if (GetNewJsonVersion() == CURRENT_RES_JSON_VERSION)
                    {
                        if (json.has("data"))
                        {
                            JSONArray jsonArr = json.getJSONArray("data");
                            if (jsonArr != null)
                            {
                                int arrLen = jsonArr.length();
                                ResInfoType item;
                                Object obj;
                                for (int i = 0; i < arrLen; i++)
                                {
                                    obj = jsonArr.get(i);
                                    if (obj instanceof JSONObject)
                                    {
                                        item = ReadResItem((JSONObject) obj, true);
                                        if (item != null && CheckIntact(item))
                                        {
                                            if (out == null) {
                                                out = MakeResInfoArrObj();
                                            }
                                            ResInfoArrAddItem(out, item);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
        return out;
    }

    public static <T extends BaseInfo> T HasItem(ArrayList<T> resArr, int id)
    {
        if (resArr != null)
        {
            int len = resArr.size();
            for (int i = 0; i < len; i++)
            {
                if (resArr.get(i).id == id)
                {
                    return resArr.get(i);
                }
            }
        }
        return null;
    }

    public static <T extends BaseInfo> T DeleteItem(ArrayList<T> resArr, int id)
    {
        T t = HasItem(resArr, id);
        if(t != null)
        {
            resArr.remove(t);
        }

        return t;
    }

    public boolean CheckIntact(ResInfoType item)
    {
        return true;
    }

    public abstract ResInfoArrType GetResArrByInfoFilter(Context context, InfoFilter filter);

    protected abstract ResInfoArrType CheckSdcardInfoRes(Context context, @Nullable ResInfoArrType arr);

    protected abstract String GetSdcardPath(Context context);

    protected abstract int GetNewJsonVersion();

    protected abstract ResInfoType ReadResItem(JSONObject jsonObject, boolean isPath);

    protected abstract boolean SaveSdcardRes(Context context, ResInfoArrType arr);

    protected abstract ResInfoArrType GetLocalInfoRes(Context context);

    protected abstract ResInfoArrType GetLocalInfoResLive(Context context);

    protected abstract ResInfoArrType GetLocalInfoResCamera(Context context);

}
