package cn.poco.resource;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;

import com.adnonstop.resourcelibs.DataFilter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;

import cn.poco.framework.EventID;
import cn.poco.resource.protocol.MaterialResourceProtocol;
import cn.poco.resource.protocol.PageType;
import cn.poco.resource.protocol.ResourceGroup;
import cn.poco.system.AppInterface;

/**
 * 直播动态贴纸分类标签 小红点
 *
 * @author lmx
 *         Created by lmx on 2017/10/10.
 */

public class LiveVideoStickerGroupResRedDotMrg2 extends BaseResMgr<LiveVideoStickerRedDotRes, SparseArray<LiveVideoStickerRedDotRes>>
{

    public static final int NEW_JSON_VER = 1;//FIXME sd卡json版本号，若结构改动需升级版本号
    public static final int CURRENT_RES_JSON_VER = 1;

    public static final String CLOUD_URL = "http://open.adnonstop.com/app_source/biz/prod/api/public/index.php?r=Switch/RedDotList";
//    public static final String CLOUD_URL = "http://tw.adnonstop.com/beauty/app/api/app_source/biz/beta/api/public/index.php?r=Switch/RedDotList";


    public static final String SDCARD_PATH = DownloadMgr.getInstance().RESOURCE_RED_DOT_PATH + "/live_sticker_group_red_dot.xxxx";
    public static final String CLOUD_CACHE_STICKER_GROUP_PATH = DownloadMgr.getInstance().RESOURCE_RED_DOT_PATH + "/live_sticker_group_red_dot_cache.xxxx";

    /**
     * 包括sd和网络更新部分（sd和网络id相同情况下，优先网络）
     */
    public SparseArray<LiveVideoStickerRedDotRes> mAllResArr;

    public boolean mHasAllResArr = false;


    private static LiveVideoStickerGroupResRedDotMrg2 sInstance;

    private LiveVideoStickerGroupResRedDotMrg2()
    {
    }


    public synchronized static LiveVideoStickerGroupResRedDotMrg2 getInstance()
    {
        if (sInstance == null)
        {
            sInstance = new LiveVideoStickerGroupResRedDotMrg2();
        }
        return sInstance;
    }

    public SparseArray<LiveVideoStickerRedDotRes> getAllResArr(Context context)
    {
        if (!mHasAllResArr || mAllResArr == null || mAllResArr.size() == 0)
        {
            long s = System.currentTimeMillis();

            SparseArray<LiveVideoStickerRedDotRes> downloadArr = sync_ar_GetCloudCacheRes(context, null);
            SparseArray<LiveVideoStickerRedDotRes> sdcardArr = sync_GetSdcardRes(context, null);

            if (downloadArr != null && downloadArr.size() > 0)
            {
                SparseArray<LiveVideoStickerRedDotRes> temp_arr = new SparseArray<>();

                //网络res
                for (int i = 0, size = downloadArr.size(); i < size; i++)
                {
                    LiveVideoStickerRedDotRes download_res = downloadArr.valueAt(i);
                    if (download_res != null)
                    {
                        download_res.m_show_new = true;
                        temp_arr.put(download_res.m_id, download_res);
                    }
                }

                //合并已标记的资源，更新网络最新数据
                if (sdcardArr != null && sdcardArr.size() > 0)
                {
                    for (int i = 0, size = sdcardArr.size(); i < size; i++)
                    {
                        LiveVideoStickerRedDotRes sdcard_res = sdcardArr.valueAt(i);
                        LiveVideoStickerRedDotRes download_res = temp_arr.get(sdcard_res.m_id);

                        if (download_res != null)
                        {
                            temp_arr.remove(download_res.m_id);
                            if (sdcard_res.m_timestamp != download_res.m_timestamp)
                            {
                                //时间戳不一致，更新小红点
                                sdcard_res.m_tips = download_res.m_tips;
                                sdcard_res.m_timestamp = download_res.m_timestamp;
                                sdcard_res.m_show_new = true;
                            }
                            else
                            {
                                sdcard_res.m_show_new = false;
                            }
                        }
                        else
                        {
                            sdcard_res.m_show_new = false;
                        }

                        temp_arr.put(sdcard_res.m_id, sdcard_res);
                    }
                }

                mHasAllResArr = true;
                mAllResArr = temp_arr;
                //Log.d("bbb", "VideoStickerGroupResRedDotMrg2 --> getAllResArr: " + (System.currentTimeMillis() - s));
            }
        }

        return mAllResArr;
    }

    /**
     * 是否有小红点 更新
     *
     * @param context
     * @param id
     * @return
     */
    public boolean hasMarkFlag(Context context, int id)
    {
        boolean out = false;
        SparseArray<LiveVideoStickerRedDotRes> allResArr = getAllResArr(context);

        if (allResArr != null)
        {
            LiveVideoStickerRedDotRes res = allResArr.get(id);
            if (res != null && res.m_show_new)
            {
                out = true;
            }
        }

        return out;
    }

    /**
     * 标记id 资源为已读，写到sd上
     *
     * @param context
     * @param id
     */
    public void markResFlag(Context context, int id)
    {
        SparseArray<LiveVideoStickerRedDotRes> allResArr = getAllResArr(context);
        if (allResArr != null)
        {
            LiveVideoStickerRedDotRes markRes = allResArr.get(id);
            SparseArray<LiveVideoStickerRedDotRes> sdcard_arr = sync_GetSdcardRes(context, null);
            if (sdcard_arr != null && markRes != null && markRes.m_show_new)
            {
                markRes.m_show_new = false;
                sdcard_arr.put(markRes.m_id, markRes);
                sync_SaveSdcardRes(context, sdcard_arr);
            }
        }
    }


    /**
     * 资源res 网络请求
     *
     * @param context
     * @param filter
     * @return
     */
    @Override
    protected Object sync_raw_ReadCloudData(Context context, DataFilter filter)
    {
        byte[] data = null;
        try
        {
            /**
             *直播动态贴纸分类标签 小红点 请求
             */
            data = MaterialResourceProtocol.Get(CLOUD_URL,
                    MaterialResourceProtocol.MATERIAL_RESOURCE_SERVER_VERSION,
                    false,
                    AppInterface.GetInstance(context).GetMKey(),
                    MaterialResourceProtocol.GetReqParams(PageType.LIVE_STICKER_TAG, MaterialResourceProtocol.IS_DEBUG, new ResourceGroup[]{ResourceGroup.DOWNLOAD}),
                    MaterialResourceProtocol.GetReqComeFromParams(context), null);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }

        return data;
    }

    @Override
    protected void sync_raw_WriteCloudData(Context context, DataFilter filter, Object data)
    {
        super.sync_raw_WriteCloudData(context, filter, data);
    }

    @Override
    protected SparseArray<LiveVideoStickerRedDotRes> sync_DecodeCloudRes(Context context, DataFilter filter, Object data)
    {
        SparseArray<LiveVideoStickerRedDotRes> out = null;
        try
        {
            JSONObject jsonObject = new JSONObject(new String((byte[]) data));
            JSONArray jsonArr = VideoStickerResMgr2.GetRetDataList(jsonObject);
            LiveVideoStickerRedDotRes item;
            Object obj;
            out = new SparseArray<>();
            int arrLen = jsonArr.length();
            for (int i = 0; i < arrLen; i++)
            {
                obj = jsonArr.get(i);
                if (obj instanceof JSONObject)
                {
                    item = ReadResItem((JSONObject) obj, false);
                    if (item != null)
                    {
                        out.put(item.m_id, item);
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

    @Override
    public int GetResArrSize(SparseArray<LiveVideoStickerRedDotRes> arr)
    {
        if (arr != null)
        {
            return arr.size();
        }
        return 0;
    }

    @Override
    public SparseArray<LiveVideoStickerRedDotRes> MakeResArrObj()
    {
        return new SparseArray<>();
    }

    @Override
    public boolean ResArrAddItem(SparseArray<LiveVideoStickerRedDotRes> arr, LiveVideoStickerRedDotRes item)
    {
        if (arr != null && item != null)
        {
            arr.put(item.m_id, item);
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    protected SparseArray<LiveVideoStickerRedDotRes> sync_raw_GetLocalRes(Context context, DataFilter filter)
    {
        //TODO 内置素材
        return null;
    }

    /**
     * 将已标记过小红点 写到sd卡上
     *
     * @param context
     * @param arr
     */
    @Override
    protected void sync_raw_SaveSdcardRes(Context context, SparseArray<LiveVideoStickerRedDotRes> arr)
    {
        FileOutputStream fos = null;
        try
        {
            JSONObject jsonObject = new JSONObject();
            {
                jsonObject.put("ver", NEW_JSON_VER);

                if (arr != null)
                {
                    LiveVideoStickerRedDotRes res;
                    JSONObject jo;
                    JSONArray ja = new JSONArray();
                    int size = arr.size();
                    for (int i = 0; i < size; i++)
                    {
                        res = arr.valueAt(i);
                        if (res != null)
                        {
                            jo = new JSONObject();
                            jo.put("id", Integer.toString(res.m_id));
                            jo.put("timestamp", Long.toString(res.m_timestamp));
                            jo.put("tip", res.m_tips != null ? res.m_tips : "");
                            ja.put(jo);
                        }
                    }
                    jsonObject.put("data", ja);
                }
            }

            fos = new FileOutputStream(GetSdcardPath(context));
            fos.write(jsonObject.toString().getBytes());
            fos.flush();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
        finally
        {
            if (fos != null)
            {
                try
                {
                    fos.close();
                }
                catch (Throwable e)
                {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    protected int GetCloudEventId()
    {
        return EventID.LIVE_RED_DOT_STICKER_TAG_CLOUD_OK;
    }

    @Override
    protected int GetNewOrderJsonVer()
    {
        return 0;
    }

    @Override
    protected int GetNewJsonVer()
    {
        return NEW_JSON_VER;
    }

    @Override
    protected LiveVideoStickerRedDotRes ReadResItem(JSONObject jsonObject, boolean isPath)
    {
        LiveVideoStickerRedDotRes out = null;

        if (jsonObject != null)
        {
            try
            {
                out = new LiveVideoStickerRedDotRes();

                if (isPath)
                {
                    out.m_type = BaseRes.TYPE_LOCAL_PATH;
                }
                else
                {
                    out.m_type = BaseRes.TYPE_NETWORK_URL;
                }

                String temp;
                //id
                if (jsonObject.has("id"))
                {
                    temp = jsonObject.getString("id");
                    if (!TextUtils.isEmpty(temp))
                    {
                        out.m_id = (int) Long.parseLong(temp);
                    }
                    else
                    {
                        out.m_id = (int) (Math.random() * 10000000);
                    }
                }

                //timestamp
                if (jsonObject.has("timestamp"))
                {
                    temp = jsonObject.getString("timestamp");
                    if (!TextUtils.isEmpty(temp))
                    {
                        out.m_timestamp = Long.parseLong(temp);
                    }
                    else
                    {
                        out.m_timestamp = System.currentTimeMillis();
                    }
                }

                //tips
                if (jsonObject.has("tips"))
                {
                    temp = jsonObject.getString("tips");
                    if (!TextUtils.isEmpty(temp))
                    {
                        out.m_tips = temp;
                    }
                    else
                    {
                        out.m_tips = null;
                    }
                }
            }
            catch (Throwable t)
            {

            }
        }

        return out;

    }

    @Override
    protected String GetSdcardPath(Context context)
    {
        return SDCARD_PATH;
    }

    @Override
    protected String GetCloudUrl(Context context)
    {
        return CLOUD_URL;
    }

    @Override
    protected String GetCloudCachePath(Context context)
    {
        return CLOUD_CACHE_STICKER_GROUP_PATH;
    }

    @Override
    public LiveVideoStickerRedDotRes GetItem(SparseArray<LiveVideoStickerRedDotRes> arr, int id)
    {
        if (arr != null)
        {
            return arr.get(id);
        }
        return null;
    }
}
