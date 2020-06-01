package cn.poco.camera3.beauty.data;

import android.content.Context;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.ArrayList;

import cn.poco.resource.DownloadMgr;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.CommonUtils;
import cn.poco.utils.FileUtil;

/**
 * 若预设数据变更、结构变化、恢复初始化数据，提升{@link #NEW_JSON_VERSION}
 *
 * @author lmx
 *         Created by lmx on 2018-01-17.
 */

public class BeautyResMgr extends BaseShapeResMgr<BeautyInfo, ArrayList<BeautyInfo>>
{

    //TODO json结构版本号
    public static final int NEW_JSON_VERSION = 1;

    protected final String SDCARD_PATH = DownloadMgr.getInstance().SHAPE_DATA_PATH + "/beauty_data.xxxx";


    public static BeautyResMgr sInstance;

    private BeautyResMgr()
    {
    }

    public static synchronized BeautyResMgr getInstance()
    {
        if (sInstance == null)
        {
            sInstance = new BeautyResMgr();
        }
        return sInstance;
    }

    @Override
    public int GetResInfoArrSize(ArrayList<BeautyInfo> arr)
    {
        return arr == null ? 0 : arr.size();
    }

    @Override
    public ArrayList<BeautyInfo> MakeResInfoArrObj()
    {
        return new ArrayList<>();
    }

    @Override
    public boolean ResInfoArrAddItem(ArrayList<BeautyInfo> arr, BeautyInfo item)
    {
        return arr != null && arr.add(item);
    }

    @Override
    protected ArrayList<BeautyInfo> CheckSdcardInfoRes(Context context, @Nullable ArrayList<BeautyInfo> arr)
    {
        //判断系统数据是否被清除，需要是否恢复初始化数据
        boolean isNew = TagMgr.CheckTag(context, Tags.RESOURCE_BEAUTY_VERSION);
        int vc = TagMgr.GetTagIntValue(context, Tags.RESOURCE_BEAUTY_VERSION);
        if (isNew)
        {
            FileUtil.deleteSDFile(GetSdcardPath(context));
            arr = null;
        }
        TagMgr.SetTagValue(context, Tags.RESOURCE_BEAUTY_VERSION, String.valueOf(CommonUtils.GetAppVerCode(context)));

        if (arr == null)
        {
            //获取sdcard数据文件不存在，加载预设内置数据
            arr = GetLocalInfoRes(context);
            //写到sdcard下
            SyncSaveSdcardArr(context, arr);
        }
        return arr;
    }

    @Override
    public ArrayList<BeautyInfo> GetResArrByInfoFilter(Context context, InfoFilter filter)
    {
        ArrayList<BeautyInfo> out = SyncGetSdcardArr(context);
        if (filter != null)
        {
            //TODO filter 处理
        }
        return out;
    }

    @Override
    protected String GetSdcardPath(Context context)
    {
        return SDCARD_PATH;
    }

    @Override
    protected int GetNewJsonVersion()
    {
        return NEW_JSON_VERSION;
    }

    @Override
    protected BeautyInfo ReadResItem(JSONObject jsonObject, boolean isPath)
    {
        BeautyInfo out = null;
        if (jsonObject != null)
        {
            try
            {
                out = new BeautyInfo();
                if (jsonObject.has("id"))
                {
                    out.setId(jsonObject.getInt("id"));
                }
                if (jsonObject.has("name"))
                {
                    out.setName(jsonObject.getString("name"));
                }
                if (jsonObject.has("resType"))
                {
                    out.setResType(jsonObject.getInt("resType"));
                }
                if (jsonObject.has("showType"))
                {
                    out.setShowType(jsonObject.getInt("showType"));
                }
                BeautyData data = new BeautyData();
                if (jsonObject.has("skinBeauty"))
                {
                    data.setSkinBeauty((float) jsonObject.getDouble("skinBeauty"));
                }
                if (jsonObject.has("whitenTeeth"))
                {
                    data.setWhitenTeeth((float) jsonObject.getDouble("whitenTeeth"));
                }
                if (jsonObject.has("skinType"))
                {
                    data.setSkinType((float) jsonObject.getDouble("skinType"));
                }
                out.setData(data);
            }
            catch (Throwable t)
            {
                t.printStackTrace();
            }
        }
        return out;
    }

    @Override
    protected boolean SaveSdcardRes(Context context, ArrayList<BeautyInfo> arr)
    {
        boolean result = false;
        FileOutputStream fos = null;
        try
        {
            JSONObject json = new JSONObject();
            json.put("version", NEW_JSON_VERSION);
            JSONArray jsonArr = new JSONArray();
            if (arr != null)
            {
                for (BeautyInfo info : arr)
                {
                    JSONObject resJson = new JSONObject();
                    resJson.put("id", info.getId());
                    resJson.put("name", info.getName() == null ? "" : info.getName());
                    resJson.put("resType", info.getResType());
                    resJson.put("showType", info.getShowType());

                    resJson.put("skinBeauty", info.getData().getSkinBeauty());
                    resJson.put("whitenTeeth", info.getData().getWhitenTeeth());
                    resJson.put("skinType", info.getData().getSkinType());
                    jsonArr.put(resJson);
                }
            }
            json.put("data", jsonArr);
            fos = new FileOutputStream(GetSdcardPath(context));
            fos.write(json.toString().getBytes());
            fos.flush();
            result = true;
        }
        catch (Throwable t)
        {
            fos = null;
            t.printStackTrace();
            result = false;
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
        return false;
    }

    @Override
    protected ArrayList<BeautyInfo> GetLocalInfoRes(Context context)
    {
        //NOTE 目前镜头社区和直播助手的美颜数据共用
        //TODO 初始化预设内置数据
        ArrayList<BeautyInfo> out = MakeResInfoArrObj();
        ArrayList<BeautyInfo> live = GetLocalInfoResLive(context);
        if (live != null && live.size() > 0)
        {
            out.addAll(live);
        }
        ArrayList<BeautyInfo> camera = GetLocalInfoResCamera(context);
        if (camera != null && camera.size() > 0)
        {
            out.addAll(camera);
        }

        return out;
    }

    @Override
    protected ArrayList<BeautyInfo> GetLocalInfoResLive(Context context)
    {
        // TODO 初始化预设内置美颜数据（直播助手）
        return null;
    }

    @Override
    protected ArrayList<BeautyInfo> GetLocalInfoResCamera(Context context)
    {
        // TODO 初始化预设内置美颜数据（镜头社区）
        ArrayList<BeautyInfo> out = MakeResInfoArrObj();

        BeautyInfo beautyInfo = new BeautyInfo();
        beautyInfo.setResType(BaseInfo.RES_TYPE_LOCAL);
        beautyInfo.setShowType(BaseInfo.SHOW_TYPE_CAMERA);
        beautyInfo.setName("");
        beautyInfo.setData(SuperShapeData.GetDefBeautyData());
        out.add(beautyInfo);
        return out;
    }
}
