package cn.poco.camera;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * {@link #getInstance()} 后要调用{@link #init(Context)} 初始化SharedPreferences
 *
 * @author lmx
 *         Created by lmx on 2017/8/25.
 */

public class StickerSPConfig
{
    private SharedPreferences mSP;

    private static StickerSPConfig mInstance;

    public static StickerSPConfig getInstance()
    {
        if (mInstance == null)
        {
            synchronized (StickerSPConfig.class)
            {
                if (mInstance == null)
                {
                    mInstance = new StickerSPConfig();
                }
            }
        }
        return mInstance;
    }

    private StickerSPConfig()
    {
    }

    public void init(@NonNull Context context)
    {
        mSP = context.getSharedPreferences("sticker_config", Context.MODE_PRIVATE);
    }

    /**
     * 获取贴纸是否静音
     *
     * @return
     */
    public boolean getStickerMute()
    {
        if (mSP != null)
        {
            return mSP.getBoolean("sticker_mute", false);
        }
        return false;
    }

    /**
     * 获取变形素材的id，-1为无变形素材
     *
     * @return
     */
    public int getShapeStickerId()
    {
        if (mSP != null)
        {
            return mSP.getInt("shape_sticker_id", -1);
        }
        return -1;
    }

    public void setShapeStickerId(int shapeStickerId)
    {
        if (mSP != null)
        {
            mSP.edit().putInt("shape_sticker_id", shapeStickerId).apply();
        }
    }

    /**
     * 获取贴纸素材的id，-2为默认第一个可用贴纸素材，-1为无贴纸素材
     *
     * @return
     */
    public int getStickerId()
    {
        if (mSP != null)
        {
            return mSP.getInt("sticker_id", -2);
        }
        return -2;
    }

    /**
     * 获取贴纸分类标签的id，-1为默认贴纸素材所在的默认分类
     *
     * @return
     */
    public int getStickerCategoryId()
    {
        if (mSP != null)
        {
            return mSP.getInt("sticker_category_id", -1);
        }
        return -1;
    }

    public void setStickerMute(boolean mute)
    {
        if (mSP != null)
        {
            mSP.edit().putBoolean("sticker_mute", mute).apply();
        }
    }

    public void setStickerId(int id)
    {
        if (mSP != null)
        {
            mSP.edit().putInt("sticker_id", id).apply();
        }
    }

    public void setStickerCategoryId(int stickerCatergoryId)
    {
        if (mSP != null)
        {
            mSP.edit().putInt("sticker_category_id", stickerCatergoryId).apply();
        }
    }

    public void clearAll()
    {
        mSP = null;
        mInstance = null;
    }
}
