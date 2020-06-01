package cn.poco.camera3.beauty;

import android.content.Context;
import android.content.SharedPreferences;

import cn.poco.camera3.beauty.data.SuperShapeData;

/**
 * @author lmx
 *         Created by lmx on 2018-01-26.
 */

public class ShapeSPConfig
{

    private static ShapeSPConfig sInstance;
    private SharedPreferences mSP;

    public static ShapeSPConfig getInstance(Context context)
    {
        synchronized (ShapeSPConfig.class)
        {
            if (sInstance == null)
            {
                sInstance = new ShapeSPConfig(context);
            }
        }
        return sInstance;
    }

    private ShapeSPConfig(Context context)
    {
        if (mSP == null && context != null)
        {
            mSP = context.getSharedPreferences("shape_config", Context.MODE_PRIVATE);
        }
    }


    public void setShapeId(int shapeId)
    {
        if (mSP != null)
        {
            mSP.edit().putInt("shape_id", shapeId).apply();
        }
    }

    public int getShapeId()
    {
        if (mSP != null)
        {
            return mSP.getInt("shape_id", SuperShapeData.ID_MINE_SYNC);
        }
        else
        {
            return SuperShapeData.ID_MINE_SYNC;
        }
    }

    public void clearAll()
    {
        mSP = null;
        sInstance = null;
    }
}
