package cn.poco.resource;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.adnonstop.resourcelibs.DataFilter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import cn.poco.camera3.info.PreviewBgmInfo;
import cn.poco.dynamicSticker.DownloadState;
import cn.poco.framework.EventID;
import cn.poco.resource.protocol.MaterialResourceProtocol;
import cn.poco.resource.protocol.PageType;
import cn.poco.resource.protocol.ResourceGroup;
import cn.poco.system.AppInterface;
import cn.poco.utils.FileUtil;
import my.beautyCamera.R;

/**
 * Created by Raining on 2017/10/9.
 */

public class PreviewBgmResMgr2 extends BaseResMgr<PreviewBgmRes, ArrayList<PreviewBgmRes>>
{
    private static final String TAG = "xxx";

    public static final int NEW_JSON_VER = 1;//json版本号
    public final static int NEW_ORDER_JSON_VER = 1;

    private final String SDCARD_PATH = DownloadMgr.getInstance().PREVIEW_BGM_PATH + "/preview_bgm.xxxx"; // 已下载

    private final String CLOUD_CACHE_PATH = DownloadMgr.getInstance().PREVIEW_BGM_PATH + "/cache.xxxx"; // 旧json
    private final String CLOUD_URL = "http://open.adnonstop.com/app_source/biz/prod/api/public/index.php?r=Template/GetTemplateList";

    private static final String SUCCESS_MSG = "Success!";
    private static final String FAILED_MSG = "error";

    private static PreviewBgmResMgr2 sInstance;

    private PreviewBgmResMgr2()
    {
    }

    public synchronized static PreviewBgmResMgr2 getInstance()
    {
        if (sInstance == null)
        {
            sInstance = new PreviewBgmResMgr2();
        }
        return sInstance;
    }

    @Override
    public int GetResArrSize(ArrayList<PreviewBgmRes> arr)
    {
        return arr.size();
    }

    @Override
    public ArrayList<PreviewBgmRes> MakeResArrObj()
    {
        return new ArrayList<>();
    }

    @Override
    public boolean ResArrAddItem(ArrayList<PreviewBgmRes> arr, PreviewBgmRes item)
    {
        return arr.add(item);
    }

    @Override
    protected ArrayList<PreviewBgmRes> sync_raw_GetLocalRes(Context context, DataFilter filter)
    {
        return null;
    }

    @Override
    public boolean CheckIntact(PreviewBgmRes res)
    {
        boolean out = false;

        if (res != null)
        {
            if (ResourceUtils.HasIntact(res.m_thumb) && ResourceUtils.HasIntact(res.m_res))
            {
                out = true;
            }
        }

        return out;
    }

    @Override
    protected void sync_raw_SaveSdcardRes(Context context, ArrayList<PreviewBgmRes> arr)
    {
        FileOutputStream fos = null;

        try
        {
            JSONObject json = new JSONObject();
            {
                json.put("ver", NEW_JSON_VER);

                JSONArray jArr = new JSONArray();
                {
                    if (arr != null)
                    {
                        for (PreviewBgmRes res : arr)
                        {
                            if (res != null)
                            {
                                JSONObject resJson = new JSONObject();
                                resJson.put("id", Integer.toString(res.m_id));
                                resJson.put("self_article_id", Integer.toString(res.m_production_id));
                                resJson.put("tjid", Integer.toString(res.m_tjId));
                                resJson.put("name", res.m_name != null ? res.m_name : "");
                                resJson.put("thumb", (res.m_thumb != null && res.m_thumb instanceof String) ? res.m_thumb : "");
                                resJson.put("res", (res.m_res != null && res.m_res instanceof String) ? res.m_res : "");
                                resJson.put("res_name", !TextUtils.isEmpty(res.m_res_name) ? res.m_res_name : "");
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
        catch (Throwable e)
        {
            e.printStackTrace();
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
        return EventID.PREVIEW_BGM_CLOUD_OK;
    }

    @Override
    protected int GetNewOrderJsonVer()
    {
        return NEW_ORDER_JSON_VER;
    }

    @Override
    protected PreviewBgmRes ReadResItem(JSONObject jsonObj, boolean isPath)
    {
        PreviewBgmRes out = null;

        if (jsonObj != null)
        {
            out = new PreviewBgmRes();
            try
            {
                if (jsonObj.has("id"))
                {
                    out.m_id = jsonObj.getInt("id");
                }

                if (jsonObj.has("self_article_id"))
                {
                    out.m_production_id = jsonObj.getInt("self_article_id");
                }

                if (jsonObj.has("tjid"))
                {
                    out.m_tjId = jsonObj.getInt("tjid");
                }

                if (jsonObj.has("thumb"))
                {
                    out.url_thumb = jsonObj.getString("thumb");
                }

                if (jsonObj.has("name"))
                {
                    out.m_name = jsonObj.getString("name");
                }

                if (jsonObj.has("res"))
                {
                    out.url_res = jsonObj.getString("res");
                }

                out.m_type = isPath ? BaseRes.TYPE_LOCAL_PATH : BaseRes.TYPE_NETWORK_URL;

                if (isPath && jsonObj.has("res_name"))
                {
                    out.m_res_name = jsonObj.getString("res_name");
                }
            }
            catch (Throwable e)
            {
                out = null;
                e.printStackTrace();
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
					MaterialResourceProtocol.GetReqParams(PageType.BGM, MaterialResourceProtocol.IS_DEBUG, new ResourceGroup[]{ResourceGroup.DOWNLOAD}),
					MaterialResourceProtocol.GetReqComeFromParams(context), null);
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}

        return data;
    }

    @Override
    protected ArrayList<PreviewBgmRes> sync_DecodeCloudRes(Context context, DataFilter filter, Object data)
    {
        ArrayList<PreviewBgmRes> out = null;

        try
        {
            if (data != null)
            {
                Log.d(TAG, "ReadCloudResArr: success");
                JSONObject jsonObj = new JSONObject(new String((byte[]) data));
                if (jsonObj.getInt("code") == 200 && jsonObj.getString("message").equals(SUCCESS_MSG))
                {
                    JSONObject dataObj = jsonObj.getJSONObject("data");
                    if (dataObj.getInt("ret_code") == 0)
                    {
                        JSONObject ret_data = dataObj.getJSONObject("ret_data");
                        if (ret_data != null)
                        {
                            JSONArray list = ret_data.getJSONArray("list");
                            if (list != null)
                            {
                                out = new ArrayList<>();
                                int count = list.length();
                                for (int i = 0; i < count; i++)
                                {
                                    PreviewBgmRes res = ReadResItem(list.getJSONObject(i), false);
                                    if (res != null)
                                    {
                                        out.add(res);
                                    }
                                }
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
    protected void sync_last_GetCloudRes(Context context, DataFilter filter, boolean justSave, ArrayList<PreviewBgmRes> result)
    {
        super.sync_last_GetCloudRes(context, filter, justSave, result);

        if (result != null && result.size() > 0)
        {
            PreviewBgmRes[] res = new PreviewBgmRes[result.size()];
            result.toArray(res);
            DownloadMgr.getInstance().SyncDownloadRes(res, true);
        }
    }

    @Override
    protected void sync_ui_CloudResChange(ArrayList<PreviewBgmRes> oldArr, ArrayList<PreviewBgmRes> newArr)
    {
        super.sync_ui_CloudResChange(oldArr, newArr);

        if (newArr != null && newArr.size() > 0)
        {
            for (PreviewBgmRes res : newArr)
            {
                DownloadMgr.FastDownloadRes(res, true);
            }
        }
    }

    @Override
    public ArrayList<PreviewBgmRes> sync_GetCloudCacheRes(Context context, DataFilter filter)
    {
        ArrayList<PreviewBgmRes> arr = mCloudResArr;
        ArrayList<PreviewBgmRes> arr2 = super.sync_GetCloudCacheRes(context, filter);

        synchronized (CLOUD_MEM_LOCK)
        {
            if (arr != arr2 && arr2 != null)
            {
                for (PreviewBgmRes res : arr2)
                {
                    DownloadMgr.FastDownloadRes(res, true);
                }
            }
        }

        return arr2;
    }

    @Override
    public PreviewBgmRes GetItem(ArrayList<PreviewBgmRes> arr, int id)
    {
        return ResourceUtils.GetItem(arr, id);
    }

    @Override
    protected void RebuildNetResArr(ArrayList<PreviewBgmRes> dst, ArrayList<PreviewBgmRes> src)
    {
        if (dst != null && src != null)
        {
            PreviewBgmRes srcTemp;
            PreviewBgmRes dstTemp;
            Class cls = PreviewBgmRes.class;
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
                    dstTemp.m_res = srcTemp.m_res;

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

    public ArrayList<PreviewBgmInfo> GetResArr(Context context)
    {
        return GetResArr(context, false);
    }

    //本地音乐info id
    public static final int BGM_INFO_LOCAL_ID = 0xFFFFFFF4;

    //无音乐info id
    public static final int BGM_INFO_NON_ID = 0;

    //本地被选中音乐info id
    public static final int BMG_INFO_LOCAL_SELECT_ID = 0xFFFFFFF8;

    public ArrayList<PreviewBgmInfo> GetResArr(Context context, boolean isShowLocal)
    {
        ArrayList<PreviewBgmInfo> out = new ArrayList<>();
        ArrayList<PreviewBgmRes> downloadArr = sync_ar_GetCloudCacheRes(context, null);
        if (downloadArr != null)
        {
            for (PreviewBgmRes res : downloadArr)
            {
                PreviewBgmInfo info = new PreviewBgmInfo();
                info.setId(res.m_id);
                info.setName(res.m_name);
                info.setProducId(res.m_production_id);

                String path = res.getThumbPath();
                boolean exists = FileUtil.isFileExists(path);
                info.setThumb(exists ? path : res.url_thumb);

                path = res.getBgmPath();
                exists = FileUtil.isFileExists(path);

                info.setRes(exists ? path : null);
                info.setState(exists ? DownloadState.HAVE_DOWNLOADED : DownloadState.NEED_DOWNLOAD);

                info.setResName(res.m_res_name);

                info.setTjid(res.m_tjId);

                info.setEx(res);
                out.add(info);
            }
        }

        PreviewBgmInfo nonInfo = new PreviewBgmInfo();
        nonInfo.setId(BGM_INFO_NON_ID);
        nonInfo.setName(context != null ? context.getString(R.string.lightapp06_video_bgm_non) : "无");
        nonInfo.setIsSel(true);
        nonInfo.setThumb(R.drawable.bgm_non);
        nonInfo.setState(DownloadState.HAVE_DOWNLOADED);
        out.add(0, nonInfo);

        if (isShowLocal)
        {
            nonInfo = new PreviewBgmInfo();
            nonInfo.setId(BGM_INFO_LOCAL_ID);
            nonInfo.setName(context != null ? context.getString(R.string.lightapp06_video_bgm_local) : "本地音乐");
            nonInfo.setIsSel(false);
            nonInfo.setThumb(R.drawable.bgm_local);
            nonInfo.setState(DownloadState.HAVE_DOWNLOADED);
            out.add(1, nonInfo);
        }

        return out;
    }
}
