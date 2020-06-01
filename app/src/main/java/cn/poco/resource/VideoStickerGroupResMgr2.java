package cn.poco.resource;

import android.content.Context;
import android.text.TextUtils;

import com.adnonstop.resourcelibs.DataFilter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import cn.poco.dynamicSticker.ShowType;
import cn.poco.framework.EventID;
import cn.poco.framework.MyFramework2App;
import cn.poco.resource.protocol.MaterialResourceProtocol;
import cn.poco.resource.protocol.PageType;
import cn.poco.resource.protocol.ResourceGroup;
import cn.poco.system.AppInterface;

/**
 * Created by Raining on 2017/10/8.
 */

public class VideoStickerGroupResMgr2 extends BaseResMgr<VideoStickerGroupRes, ArrayList<VideoStickerGroupRes>>
{
    public static final int NEW_JSON_VER = 4;//FIXME 动态贴纸sd卡json版本号，若结构改动需升级版本号;
    public final static int NEW_ORDER_JSON_VER = 4;

    protected final String SDCARD_PATH = DownloadMgr.getInstance().VIDEO_FACE_PATH + "/video_face_group.xxxx";

    protected final String CLOUD_CACHE_PATH = DownloadMgr.getInstance().VIDEO_FACE_PATH + "/video_face_group_cache.xxxx";
    protected final String CLOUD_URL = "http://open.adnonstop.com/app_source/biz/prod/api/public/index.php?r=Template/GetTemplateList";

    //test 内测
//    protected final String CLOUD_URL = "http://tw.adnonstop.com/beauty/app/api/app_source/biz/beta/api/public/index.php?r=Template/GetTemplateList";

    private static VideoStickerGroupResMgr2 sInstance;

    private VideoStickerGroupResMgr2()
    {
    }

    public synchronized static VideoStickerGroupResMgr2 getInstance()
    {
        if (sInstance == null)
        {
            sInstance = new VideoStickerGroupResMgr2();
        }
        return sInstance;
    }

    @Override
    public int GetResArrSize(ArrayList<VideoStickerGroupRes> arr)
    {
        return arr.size();
    }

    @Override
    public ArrayList<VideoStickerGroupRes> MakeResArrObj()
    {
        return new ArrayList<>();
    }

    @Override
    public boolean ResArrAddItem(ArrayList<VideoStickerGroupRes> arr, VideoStickerGroupRes item)
    {
        return arr.add(item);
    }

    @Override
    protected ArrayList<VideoStickerGroupRes> sync_raw_GetLocalRes(Context context, DataFilter filter)
    {
        ArrayList<VideoStickerGroupRes> out = new ArrayList<>();
        return out;
    }

    @Override
    protected void sync_raw_SaveSdcardRes(Context context, ArrayList<VideoStickerGroupRes> arr)
    {
        FileOutputStream fos = null;

        try
        {
            JSONObject json = new JSONObject();
            {
                json.put("ver", NEW_JSON_VER);

                JSONArray jArr = new JSONArray();//data[]
                {
                    if (arr != null)
                    {
                        JSONObject resJson = null;

                        for (VideoStickerGroupRes groupRes : arr)
                        {
                            if (groupRes != null)
                            {
                                resJson = new JSONObject();
                                resJson.put("id", Integer.toString(groupRes.m_id));
                                resJson.put("pushID", groupRes.m_tjId);
                                resJson.put("name", groupRes.m_name);
                                resJson.put("isHide", groupRes.m_isHide);
                                resJson.put("thumb", (groupRes.m_thumb != null && groupRes.m_thumb instanceof String) ? groupRes.m_thumb : "");
                                resJson.put("display", groupRes.m_display);
                                JSONArray idsArr = new JSONArray();

                                if (groupRes.m_stickerIDArr != null && groupRes.m_stickerIDArr.length > 0)
                                {
                                    for (int i = 0; i < groupRes.m_stickerIDArr.length; i++)
                                    {
                                        idsArr.put(groupRes.m_stickerIDArr[i]);
                                    }
                                }
                                resJson.put("group", idsArr);
                                jArr.put(resJson);
                            }
                        }
                    }
                }
                json.put("data", jArr);
            }
            fos = new FileOutputStream(SDCARD_PATH);
            fos.write(json.toString().getBytes());
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
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected int GetCloudEventId()
    {
        return EventID.VIDEO_FACE_GROUP_CLOUD_OK;
    }

    @Override
    protected int GetNewOrderJsonVer()
    {
        return NEW_ORDER_JSON_VER;
    }

    @Override
    protected VideoStickerGroupRes ReadResItem(JSONObject jsonObject, boolean isPath)
    {
        VideoStickerGroupRes out = null;

        if (jsonObject != null)
        {
            try
            {
                out = new VideoStickerGroupRes();

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
                        out.m_id = (int) Long.parseLong(temp, 10);
                    }
                    else
                    {
                        out.m_id = (int) (Math.random() * 10000000);
                    }
                }

                //name
                if (jsonObject.has("name"))
                {

                    temp = jsonObject.getString("name");
                    if (!TextUtils.isEmpty(temp))
                    {
                        out.m_name = temp;
                    }
                }

                //pushId
                if (jsonObject.has("pushID"))
                {
                    temp = jsonObject.getString("pushID");
                    if (!TextUtils.isEmpty(temp))
                    {
                        out.m_tjId = Integer.valueOf(temp);
                    }
                }

                //isHide
                if (jsonObject.has("isHide"))
                {
                    out.m_isHide = jsonObject.getBoolean("isHide");
                }

                //display
                if (jsonObject.has("display"))
                {
                    int display = jsonObject.getInt("display");
                    if (display == ShowType.Label.ALL)
                    {
                        out.m_display = ShowType.Label.ALL;
                    }
                    else if (display == 1)
                    {
                        out.m_display = ShowType.Label.CAMERA;
                    }
                    else if (display == 2)
                    {
                        out.m_display = ShowType.Label.LIVE;
                    }
                }

                //thumb
                if (isPath)
                {
                    if (jsonObject.has("thumb"))
                    {
                        out.m_thumb = jsonObject.getString("thumb");
                    }
                }
                else
                {
                    if (jsonObject.has("thumb"))
                    {
                        out.url_thumb = jsonObject.getString("thumb");
                    }
                }

                //group ids
                if (jsonObject.has("group"))
                {
                    JSONArray jarr = jsonObject.getJSONArray("group");
                    if (jarr != null && jarr.length() > 0)
                    {
                        int len = jarr.length();
                        out.m_stickerIDArr = new int[len];
                        for (int i = 0; i < len; i++)
                        {
                            out.m_stickerIDArr[i] = jarr.getInt(i);
                        }
                    }
                }
            }
            catch (Throwable t)
            {
                out = null;
                t.printStackTrace();
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
    protected int GetNewJsonVer()
    {
        return NEW_JSON_VER;
    }

    @Override
    protected String GetCloudUrl(Context context)
    {
        return CLOUD_URL;
    }

    @Override
    protected String GetCloudCachePath(Context context)
    {
        return CLOUD_CACHE_PATH;
    }

    @Override
    protected Object sync_raw_ReadCloudData(Context context, DataFilter filter)
    {
        byte[] data = null;

        try
        {
            data = MaterialResourceProtocol.Get(
                    GetCloudUrl(context),
                    MaterialResourceProtocol.MATERIAL_RESOURCE_SERVER_VERSION,
                    false,
                    AppInterface.GetInstance(context).GetMKey(),
                    MaterialResourceProtocol.GetReqParams(
                            PageType.STICKER_TAG,
                            MaterialResourceProtocol.IS_DEBUG,
                            new ResourceGroup[]{ResourceGroup.LOCAL_RES, ResourceGroup.DOWNLOAD}),
                    MaterialResourceProtocol.GetReqComeFromParams(context), null);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }

        return data;
    }

    @Override
    protected ArrayList<VideoStickerGroupRes> sync_DecodeCloudRes(Context context, DataFilter filter, Object data)
    {
        ArrayList<VideoStickerGroupRes> out = null;

        try
        {
            if (data != null)
            {
                JSONObject jsonObject = new JSONObject(new String((byte[]) data));
                JSONArray listArr = VideoStickerResMgr2.GetRetDataList(jsonObject);
                if (listArr != null)
                {
                    out = new ArrayList<>();

                    VideoStickerGroupRes res;
                    int length = listArr.length();
                    for (int i = 0; i < length; i++)
                    {
                        Object obj = listArr.get(i);
                        if (obj != null && obj instanceof JSONObject)
                        {
                            res = ReadResItem((JSONObject) obj, false);
                            if (res != null)
                            {
                                out.add(res);
                            }
                        }
                    }
                }
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }

        return out;
    }

    @Override
    public ArrayList<VideoStickerGroupRes> sync_GetCloudCacheRes(Context context, DataFilter filter)
    {
        ArrayList<VideoStickerGroupRes> arr = mCloudResArr;
        ArrayList<VideoStickerGroupRes> arr2 = super.sync_GetCloudCacheRes(context, filter);

        synchronized (CLOUD_MEM_LOCK)
        {
            if (arr != arr2 && arr2 != null)
            {
                ArrayList<VideoStickerGroupRes> localArr = sync_GetLocalRes(context, null);
                ArrayList<VideoStickerGroupRes> sdcardArr = sync_GetSdcardRes(context, null);
                BuildGroupArr(arr2, localArr);
                BuildGroupArr(arr2, sdcardArr);
            }
        }

        return arr2;
    }

    @Override
    protected void sync_last_GetCloudRes(Context context, DataFilter filter, boolean justSave, ArrayList<VideoStickerGroupRes> result)
    {
        super.sync_last_GetCloudRes(context, filter, justSave, result);

        // if (result != null && result.size() > 0)
        // {
        //     //下载图标
        //     VideoStickerGroupRes[] arr2 = new VideoStickerGroupRes[result.size()];
        //     result.toArray(arr2);
        //     DownloadMgr.getInstance().SyncDownloadRes(arr2, true); //同步下载，线程阻塞
        // }
    }

    @Override
    protected void sync_ui_CloudResChange(ArrayList<VideoStickerGroupRes> oldArr, ArrayList<VideoStickerGroupRes> newArr)
    {
        super.sync_ui_CloudResChange(oldArr, newArr);

        if (newArr != null && newArr.size() > 0)
        {
            Context context = MyFramework2App.getInstance().getApplicationContext();
            ArrayList<VideoStickerGroupRes> localArr = sync_GetLocalRes(context, null);
            ArrayList<VideoStickerGroupRes> sdcardArr = sync_GetSdcardRes(context, null);
            BuildGroupArr(newArr, localArr);
            BuildGroupArr(newArr, sdcardArr);
        }
    }

    @Override
    public VideoStickerGroupRes GetItem(ArrayList<VideoStickerGroupRes> arr, int id)
    {
        return ResourceUtils.GetItem(arr, id);
    }

    @Override
    protected void RebuildNetResArr(ArrayList<VideoStickerGroupRes> dst, ArrayList<VideoStickerGroupRes> src)
    {
        if (dst != null && src != null)
        {
            VideoStickerGroupRes srcTemp;
            VideoStickerGroupRes dstTemp;
            Class cls = VideoStickerGroupRes.class;
            Field[] fields = cls.getDeclaredFields();
            int index;
            int len = dst.size();
            for (int i = 0; i < len; i++)
            {
                dstTemp = dst.get(i);
                index = ResourceUtils.HasItem(src, dstTemp.m_id);
                if (index > 0)
                {
                    srcTemp = src.get(index);
                    dstTemp.m_type = srcTemp.m_type;
                    dstTemp.m_thumb = srcTemp.m_thumb;

                    for (Field field : fields)
                    {
                        try
                        {
                            if (!Modifier.isFinal(field.getModifiers()))
                            {
                                Object value = field.get(dstTemp);
                                field.set(srcTemp, value);
                            }
                        }
                        catch (Throwable e2)
                        {
                            e2.printStackTrace();
                        }
                    }
                    dst.set(i, srcTemp);
                }
            }
        }
    }

    /**
     * 判断dst是否存在src，若存在将src对象复制给dst对象，不存在则添加到dst中
     *
     * @param dst
     * @param src
     */
    private static void BuildGroupArr(ArrayList<VideoStickerGroupRes> dst, ArrayList<VideoStickerGroupRes> src)
    {
        if (dst != null && src != null)
        {
            int index;
            int len = src.size();
            VideoStickerGroupRes srcTemp;
            VideoStickerGroupRes dstTemp;

            ArrayList<VideoStickerGroupRes> merge_arr = null;

            for (int i = 0; i < len; i++)
            {
                srcTemp = src.get(i);
                index = ResourceUtils.HasItem(dst, srcTemp.m_id);
                if (index >= 0)
                {
                    dstTemp = dst.get(index);
                    dstTemp.m_type = srcTemp.m_type;
                    dstTemp.m_thumb = srcTemp.m_thumb;
                }
                else
                {
                    if (merge_arr == null)
                    {
                        merge_arr = new ArrayList<>();
                    }

                    merge_arr.add(srcTemp);
                }
            }

            if (merge_arr != null && merge_arr.size() > 0)
            {
                dst.addAll(merge_arr);
            }
        }
    }

    public void DeleteGroupRes(Context context, VideoStickerGroupRes groupRes)
    {
        if (groupRes != null && groupRes.m_stickerIDArr != null)
        {
            int[] ids = groupRes.m_stickerIDArr;
            VideoStickerResMgr2.getInstance().DeleteRes(context, ids);
        }
    }


    /**
     * 获取网络已缓存资源
     *
     * @param context
     * @param isShowHideGroup 是否包含hide标签组，true：包含{@link VideoStickerGroupRes#m_isHide}为true的标签 目前都是 false
     * @param groupIds        指定的标签id数组
     * @deprecated
     */
    public ArrayList<VideoStickerGroupRes> getCloudDownloadRes(Context context, boolean isShowHideGroup, int... groupIds)
    {
        ArrayList<VideoStickerGroupRes> out = new ArrayList<>();
        ArrayList<VideoStickerGroupRes> arr = sync_ar_GetCloudCacheRes(context, null);

        if (arr != null)
        {
            if (groupIds != null && groupIds.length > 0)
            {
                for (int groupId : groupIds)
                {
                    VideoStickerGroupRes groupRes = ResourceUtils.GetItem(arr, groupId);
                    if (groupRes != null)
                    {
                        out.add(groupRes);
                    }
                }

                //如果找不到对应的标签组，则追加全部标签
                if (out.size() == 0)
                {
                    if (isShowHideGroup)
                    {
                        out.addAll(arr);
                    }
                    else
                    {
                        for (VideoStickerGroupRes next : arr)
                        {
                            if (next != null && !next.m_isHide)
                            {
                                out.add(next);
                            }

                        }
                    }
                }
            }
            else
            {
                if (isShowHideGroup)
                {
                    out.addAll(arr);
                }
                else
                {
                    for (VideoStickerGroupRes next : arr)
                    {
                        if (next != null && !next.m_isHide)
                        {
                            out.add(next);
                        }

                    }
                }
            }
        }
        return out;
    }

    public ArrayList<VideoStickerGroupRes> getCloudDownloadRes(Context context,
                                                               boolean isShowHideGroup,
                                                               boolean isLive,
                                                               int... groupIds)
    {
        ArrayList<VideoStickerGroupRes> out = new ArrayList<>();
        ArrayList<VideoStickerGroupRes> arr = sync_ar_GetCloudCacheRes(context, null);

        if (arr != null)
        {
            if (groupIds != null && groupIds.length > 0)
            {
                for (int groupId : groupIds)
                {
                    VideoStickerGroupRes groupRes = ResourceUtils.GetItem(arr, groupId);
                    if (groupRes != null)
                    {
                        if (isLive && IsLive(groupRes))
                        {
                            out.add(groupRes);
                        }
                        else if (!isLive && ISCamera(groupRes))
                        {
                            out.add(groupRes);
                        }
                    }
                }
            }


            //如果找不到对应的标签组，则追加全部标签（除了隐藏类标签）
            if (out.size() == 0)
            {
                if (arr.size() > 0)
                {
                    for (VideoStickerGroupRes next : arr)
                    {
                        if (next != null && !next.m_isHide)
                        {
                            if (isLive && IsLive(next))
                            {
                                out.add(next);
                            }
                            else if (!isLive && ISCamera(next))
                            {
                                out.add(next);
                            }
                        }
                    }
                }
            }
        }
        return out;
    }

    private boolean IsLive(VideoStickerGroupRes groupRes)
    {
        return groupRes != null && (groupRes.m_display == ShowType.Label.ALL || groupRes.m_display == ShowType.Label.LIVE);
    }

    private boolean ISCamera(VideoStickerGroupRes groupRes)
    {
        return groupRes != null && (groupRes.m_display == ShowType.Label.ALL || groupRes.m_display == ShowType.Label.CAMERA);
    }
}
