package cn.poco.dynamicSticker;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;

import cn.poco.resource.VideoStickerRes;

/**
 * @author lmx
 *         Created by lmx on 2017/7/12.
 */

public class StickerHelper
{
    public static final String ASSET_FILE_BASE_PATH = "stickers";

    //zip包 主json
    public static final String STICKER_JSON = "sticker.json";

    /**
     * VideoStickerRes 资源解析 缓存
     */
    public static class Caches
    {
        public ArrayList<VideoStickerRes> mQueueCache;

        public int MAX_CACHE_SIZE = 5;

        public Caches()
        {
            mQueueCache = new ArrayList<>();
        }

        public void setQueueSize(int size)
        {
            if (size <= 0)
            {
                size = MAX_CACHE_SIZE;
            }
            MAX_CACHE_SIZE = size;
            int len = mQueueCache.size();
            if (len > MAX_CACHE_SIZE)
            {
                len -= MAX_CACHE_SIZE;
                for (int i = 0; i < len; i++)
                {
                    VideoStickerRes remove = mQueueCache.remove(0);
                    if (remove != null)
                    {
                        remove.mStickerRes = null;
                    }
                }
            }
        }

        public void addCache(VideoStickerRes item)
        {
            if (mQueueCache == null)
            {
                return;
            }

            mQueueCache.add(item);
            if (mQueueCache.size() > MAX_CACHE_SIZE)
            {
                VideoStickerRes remove = mQueueCache.remove(0);
                if (remove != null)
                {
                    remove.mStickerRes = null;
                }
            }
        }

        public synchronized void removeCache(VideoStickerRes item)
        {
            if (mQueueCache == null)
            {
                return;
            }

            if (mQueueCache.size() > 0 && item != null)
            {
                if (mQueueCache.contains(item))
                {
                    mQueueCache.remove(item);
                    item.mStickerRes = null;
                }
            }
        }

        public synchronized VideoStickerRes getCache(VideoStickerRes item)
        {
            if (mQueueCache == null)
            {
                return null;
            }

            if (mQueueCache.size() > 0 && item != null)
            {
                if (mQueueCache.contains(item))
                {
                    return item;
                }
            }
            return null;
        }

        public synchronized int getCacheSize()
        {
            return mQueueCache != null ? mQueueCache.size() : 0;
        }

        public synchronized void clearAll()
        {
            if (mQueueCache != null)
            {
                for (VideoStickerRes videoStickerRes : mQueueCache)
                {
                    videoStickerRes.mStickerRes = null;
                }
                mQueueCache.clear();
            }
            mQueueCache = null;
        }
    }

    /**
     * 根据id构建目标路径
     */
    public static String GetStickerIdFolderPath(VideoStickerRes res)
    {

        if (res != null)
        {
            String s = res.GetSaveParentPath() + File.separator + res.m_id;
            File file = new File(s);
            if (!file.exists())
            {
                boolean mkdirs = file.mkdirs();
            }
            return s;
        }
        return null;
    }

    /**
     * sticker zip sd 路径
     *
     * @param res
     * @return
     */
    public static String GetStickerZipPath(VideoStickerRes res)
    {
        if (res != null)
        {
            String parent = GetStickerIdFolderPath(res);
            if (parent != null)
            {
                return parent + File.separator + res.m_res_name;
            }
        }
        return null;
    }

    /**
     * sticker zip asset 路径
     *
     * @param res
     * @return
     */
    public static String GetAssetStickerZipPath(VideoStickerRes res)
    {
        if (res != null)
        {
            return ASSET_FILE_BASE_PATH + File.separator + res.m_res_name;
        }
        return null;
    }

    public static boolean isAssetFile(Context context, String fileName)
    {
        if (TextUtils.isEmpty(fileName))
        {
            return false;
        }

        if (fileName.startsWith("file:///android_asset"))
        {
            return true;
        }

        return false;
    }


}
