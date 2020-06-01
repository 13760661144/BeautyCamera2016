package cn.poco.camera3.beauty.data;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import cn.poco.framework.EventCenter;
import cn.poco.framework.EventID;
import cn.poco.login.UserMgr;
import cn.poco.loginlibs.info.UserInfo;
import cn.poco.pocointerfacelibs.PocoWebUtils;
import cn.poco.resource.DownloadMgr;
import cn.poco.setting.SettingInfo;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.system.AppInterface;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.CommonUtils;
import cn.poco.utils.FileUtil;

/**
 * 若预设数据变更、结构变化、恢复初始化数据，提升{@link #NEW_JSON_VERSION}
 *
 * @author lmx
 *         Created by lmx on 2018-01-19.
 */

public class ShapeSyncResMgr extends BaseShapeResMgr<ShapeInfo, ArrayList<ShapeInfo>>
{
    //当前同步网络版本
    public static final int CURRENT_SYNC_VERSION = 1;

    //TODO json结构版本号
    public static final int NEW_JSON_VERSION = 10;

    public final Object SYNC_LOCK = new Object();

    protected final String SDCARD_PATH = DownloadMgr.getInstance().SHAPE_DATA_PATH + "/shape_sync_data.xxxx";

    private ShapeSyncResMgr()
    {
    }

    private static ShapeSyncResMgr sInstance;

    public static synchronized ShapeSyncResMgr getInstance()
    {
        if (sInstance == null)
        {
            sInstance = new ShapeSyncResMgr();
        }
        return sInstance;
    }

    /**
     * 检测是否已修改过
     *
     * @param context
     * @return
     */
    public boolean checkSyncDataIsModify(Context context)
    {
        boolean out = false;
        ShapeInfo sdcard_shapeInfo = BaseShapeResMgr.HasItem(SyncGetSdcardArr(context), SuperShapeData.ID_MINE_SYNC);
        if (sdcard_shapeInfo != null)
        {
            out = sdcard_shapeInfo.isModify();
            if (sdcard_shapeInfo.getUpdate_time() == 1L && !out) {
                out = true;
            }
        }
        return out;
    }

    /**
     * 更新网络数据
     * 耗时操作
     *
     * @return true 更新成功
     */
    public onPostResponseInfo UpdateSyncData(Context context)
    {
        synchronized (SYNC_LOCK)
        {
            onPostResponseInfo responseInfo = new onPostResponseInfo();
            if (context == null)
            {
                responseInfo.error = onPostResponseInfo.ERROR.context_null;
                return responseInfo;
            }

            //未登录
            boolean isLogin = UserMgr.IsLogin(context, null);
            if (!isLogin)
            {
                responseInfo.error = onPostResponseInfo.ERROR.user_unlogin;
                return responseInfo;
            }

            //本地数据
            ArrayList<ShapeInfo> sdcardArr = SyncGetSdcardArr(context);
            ShapeInfo sdcard_shapeInfo = BaseShapeResMgr.HasItem(sdcardArr, SuperShapeData.ID_MINE_SYNC);
            //本地数据需同步到网络
            if (sdcard_shapeInfo != null && sdcard_shapeInfo.isNeedSynchronize())
            {
                responseInfo.isUploadSync = true;
                BeautyShapeSyncInfo callbackInfo = UploadShapeData(context, sdcard_shapeInfo, null);
                if (callbackInfo != null)
                {
                    if (callbackInfo.mProtocolCode == 200 && callbackInfo.mCode == 0)
                    {
                        //成功
                        sdcard_shapeInfo.setNeedSynchronize(false);
                        sdcard_shapeInfo.setDefaultData(false);
                        sdcard_shapeInfo.setUpdate_time();
                        SaveSdcardRes(context, sdcardArr);
                    }
                    else if (callbackInfo.mProtocolCode == 205)
                    {
                        //授权过期，重新登录
                        responseInfo.error = onPostResponseInfo.ERROR.access_token_invalid;
                    }
                }
                else
                {
                    //其他出错
                    responseInfo.error = onPostResponseInfo.ERROR.other;
                }
            }
            else
            {
                //如果用户修改数据，则不覆盖
                if (sdcard_shapeInfo != null && sdcard_shapeInfo.isModify()) return responseInfo;

                //网络数据
                BeautyShapeSyncInfo syncInfo = GetBeautyShapeApi(context, null);
                ShapeInfo net_shapeInfo = null;
                boolean isGetNetSyncSuccess = false;
                if (syncInfo != null)
                {
                    if (syncInfo.mProtocolCode == 200
                            && syncInfo.mCode == 0
                            && syncInfo.shapeInfos != null
                            && syncInfo.shapeInfos.size() > 0)
                    {
                        isGetNetSyncSuccess = true;
                        net_shapeInfo = syncInfo.shapeInfos.get(0);
                    }
                    else if (syncInfo.mProtocolCode == 205)
                    {
                        responseInfo.error = onPostResponseInfo.ERROR.access_token_invalid;
                    }
                }
                else
                {
                    responseInfo.error = onPostResponseInfo.ERROR.other;
                }
                if (isGetNetSyncSuccess && net_shapeInfo != null)
                {
                    if (sdcard_shapeInfo != null)
                    {
                        if (syncInfo.get_info_app_time > sdcard_shapeInfo.getUpdate_time())
                        {
                            if (sdcard_shapeInfo.isModify()) {
                                //如果用户修改数据，则不覆盖
                            }
                            else
                            {
                                //网络已保存数据的时间戳
                                sdcard_shapeInfo.setUpdate_time(syncInfo.get_info_app_time);
                                sdcard_shapeInfo.setNeedSynchronize(false);
                                sdcard_shapeInfo.setModify(false);
                                sdcard_shapeInfo.setDefaultData(false);
                                net_shapeInfo.getData().setEyes_type(ShapeData.EYE_TYPE.OVAL_EYES);
                                sdcard_shapeInfo.setData(net_shapeInfo.getData());
                                SaveSdcardRes(context, sdcardArr);
                            }
                        }
                    }
                    else
                    {
                        //无本地数据，将网络数据同步到本地
                        net_shapeInfo.setId(SuperShapeData.ID_MINE_SYNC);
                        net_shapeInfo.setUpdate_time(syncInfo.get_info_app_time);
                        net_shapeInfo.setNeedSynchronize(false);
                        net_shapeInfo.setModify(false);
                        net_shapeInfo.setDefaultData(false);
                        net_shapeInfo.setResType(BaseInfo.RES_TYPE_SYNC);
                        net_shapeInfo.setShowType(BaseInfo.SHOW_TYPE_ALL);
                        net_shapeInfo.getData().setEyes_type(ShapeData.EYE_TYPE.OVAL_EYES);
                        sdcardArr.add(net_shapeInfo);
                        SyncSaveSdcardArr(context, sdcardArr);
                    }
                }
            }
            return responseInfo;
        }
    }

    public final void post2UpdateSyncData(final Context context)
    {
        try
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    onPostResponseInfo responseInfo = ShapeSyncResMgr.getInstance().UpdateSyncData(context);
                    EventCenter.sendEvent(EventID.NOTIFY_SYNC_SHAPE_UPDATE, responseInfo);
                }
            }).start();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 请求网络更新
     * 自行开线程
     *
     * @param context
     * @param callback
     * @return
     */
    public static BeautyShapeSyncInfo GetBeautyShapeApi(Context context,
                                                        OnPostCallback callback)
    {
        if (context == null) return null;

        //判断是已经登录
        boolean isLogin = UserMgr.IsLogin(context, null);
        if (!isLogin) return null;

        BeautyShapeSyncInfo syncInfo = null;

        JSONObject paramJson = new JSONObject();
        UserInfo userInfo = UserMgr.ReadCache(context);
        SettingInfo settingInfo = SettingInfoMgr.GetSettingInfo(context);
        if (userInfo != null && settingInfo != null)
        {
            try
            {
                if (callback != null)
                {
                    callback.onStart();
                }

                //user_id
                //access_token
                paramJson.put("user_id", userInfo.mUserId);
                paramJson.put("access_token", settingInfo.GetPoco2Token(false));
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                if (callback != null) callback.onFailed();
            }
        }
        try
        {
            syncInfo = (BeautyShapeSyncInfo) PocoWebUtils.Post(BeautyShapeSyncInfo.class,
                    AppInterface.GetInstance(context).GetBeautyShapeApi(),
                    false, paramJson, null, null, AppInterface.GetInstance(context));
            if (callback != null)
            {
                callback.onResponse(syncInfo);
            }
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            if (callback != null) callback.onFailed();
        }
        return syncInfo;
    }

    /**
     * 上传网络
     * 自行开线程
     *
     * @param context
     * @param custom_data
     * @param callback
     */
    public BeautyShapeSyncInfo SaveBeautyShapeApi(Context context,
                                                  JSONObject custom_data,
                                                  OnPostCallback callback)
    {
        if (context == null) return null;
        boolean isLogin = UserMgr.IsLogin(context, null);
        if (!isLogin) return null;

        JSONObject paramJson = new JSONObject();
        UserInfo userInfo = UserMgr.ReadCache(context);
        SettingInfo settingInfo = SettingInfoMgr.GetSettingInfo(context);
        if (userInfo != null && settingInfo != null)
        {
            try
            {
                if (callback != null)
                {
                    callback.onStart();
                }

                //user_id
                //access_token
                //custom_data
                paramJson.put("user_id", userInfo.mUserId);
                paramJson.put("access_token", settingInfo.GetPoco2Token(false));
                paramJson.put("custom_data", custom_data != null ? custom_data.toString() : new JSONObject().toString());
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                if (callback != null) callback.onFailed();
            }

            try
            {
                BeautyShapeSyncInfo syncInfo = (BeautyShapeSyncInfo) PocoWebUtils.Post(BeautyShapeSyncInfo.class,
                        AppInterface.GetInstance(context).SaveBeautyShapeApi(),
                        false, paramJson, null, null, AppInterface.GetInstance(context));
                if (callback != null) callback.onResponse(syncInfo);
                return syncInfo;
            }
            catch (Throwable throwable)
            {
                throwable.printStackTrace();
                if (callback != null) callback.onFailed();
            }
        }
        return null;
    }

    /**
     * 上传脸型数据到网络
     *
     * @param context
     * @param shapeInfo
     * @param postCallback
     */
    public BeautyShapeSyncInfo UploadShapeData(Context context,
                                               ShapeInfo shapeInfo,
                                               OnPostCallback postCallback)
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put("version", ShapeSyncResMgr.CURRENT_SYNC_VERSION);
            jsonObject.put("shape", makeSaveShapeDatas(shapeInfo));
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }

        return SaveBeautyShapeApi(context, jsonObject, postCallback);
    }

    @Override
    public int GetResInfoArrSize(ArrayList<ShapeInfo> arr)
    {
        return arr == null ? 0 : arr.size();
    }

    @Override
    public ArrayList<ShapeInfo> MakeResInfoArrObj()
    {
        return new ArrayList<>();
    }

    @Override
    public boolean ResInfoArrAddItem(ArrayList<ShapeInfo> arr, ShapeInfo item)
    {
        return arr != null && arr.add(item);
    }

    @Override
    public ArrayList<ShapeInfo> GetResArrByInfoFilter(Context context, InfoFilter filter)
    {
        ArrayList<ShapeInfo> out = SyncGetSdcardArr(context);
        if (filter != null)
        {
            //TODO filter 处理
        }
        return out;
    }

    @Override
    protected ArrayList<ShapeInfo> CheckSdcardInfoRes(Context context, @Nullable ArrayList<ShapeInfo> arr)
    {
        //判断系统数据是否被清除，需要是否恢复初始化数据
        boolean isNew = TagMgr.CheckTag(context, Tags.RESOURCE_SHAPE_SYNC_VERSION);
        int vc = TagMgr.GetTagIntValue(context, Tags.RESOURCE_SHAPE_SYNC_VERSION);
        if (isNew)
        {
            FileUtil.deleteSDFile(GetSdcardPath(context));
            arr = null;
        }
        TagMgr.SetTagValue(context, Tags.RESOURCE_SHAPE_SYNC_VERSION, String.valueOf(CommonUtils.GetAppVerCode(context)));

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
    protected ShapeInfo ReadResItem(JSONObject jsonObject, boolean isPath)
    {
        return ShapeResMgr.ReadResourceItem(jsonObject, isPath);
    }

    @Override
    protected boolean SaveSdcardRes(Context context, ArrayList<ShapeInfo> arr)
    {
        return ShapeResMgr.SaveSdcardResource(context, GetSdcardPath(context), arr, NEW_JSON_VERSION);
    }

    @Override
    protected ArrayList<ShapeInfo> GetLocalInfoRes(Context context)
    {
        ArrayList<ShapeInfo> out = MakeResInfoArrObj();

        //我的脸型初始化参数
        ShapeInfo shapeInfo = new ShapeInfo();
        shapeInfo.setId(SuperShapeData.ID_MINE_SYNC);
        shapeInfo.setResType(BaseInfo.RES_TYPE_SYNC);
        shapeInfo.setShowType(BaseInfo.SHOW_TYPE_ALL);
        shapeInfo.setName("我的");
        shapeInfo.setModify(false);
        shapeInfo.setDefaultData(true);
        shapeInfo.setUpdate_time(1L);//第一次初始化数据
        shapeInfo.setParamsData(SuperShapeData.GetDefultMineShape());
        shapeInfo.getData().setEyes_type(ShapeData.EYE_TYPE.OVAL_EYES);
        out.add(shapeInfo);
        return out;
    }

    @Override
    protected ArrayList<ShapeInfo> GetLocalInfoResLive(Context context)
    {
        return null;
    }

    @Override
    protected ArrayList<ShapeInfo> GetLocalInfoResCamera(Context context)
    {
        return null;
    }

    public interface OnPostCallback
    {
        void onStart();

        void onFailed();

        void onResponse(@Nullable Object o);
    }

    public static class onPostResponseInfo
    {
        @IntDef({ERROR.context_null, ERROR.user_unlogin})
        @Retention(RetentionPolicy.SOURCE)
        public @interface ERROR
        {
            int context_null = 1;                   //上下文空
            int user_unlogin = 1 << 1;              //用户未登录
            int access_token_invalid = 1 << 2;      //授权失败
            int other = 1 << 3;                     //其他
        }

        public int error = 0;

        public boolean isUploadSync;
    }


    private JSONArray makeSaveShapeDatas(ShapeInfo shapeInfo)
    {
        // 保存个人脸型数据
        JSONArray jsonArray = new JSONArray();
        if (shapeInfo != null && shapeInfo.getId() == SuperShapeData.ID_MINE_SYNC)
        {
            JSONObject jsonObject = ShapeSyncResMgr.EncodeShape(shapeInfo);
            if (jsonObject != null)
            {
                jsonArray.put(jsonObject);
            }
        }

        return jsonArray;
    }


    public static JSONObject EncodeShape(ShapeInfo info)
    {
        JSONObject json = null;
        if (info != null)
        {
            json = new JSONObject();
            try
            {
                json.put("type", BaseInfo.SHOW_TYPE_ALL);
                json.put("name", info.getName());
                ShapeData data = info.getData();
                if (data != null)
                {
                    json.put("thinFace_radius", data.getThinFace_radius());
                    json.put("thinFace", data.getThinFace());
                    json.put("littleFace_radius", data.getLittleFace_radius());
                    json.put("littleFace", data.getLittleFace());
                    json.put("shavedFace_radius", data.getShavedFace_radius());
                    json.put("shavedFace", data.getShavedFace());
                    json.put("bigEye_radius", data.getBigEye_radius());
                    json.put("bigEye", data.getBigEye());
                    json.put("shrinkNose_radius", data.getShrinkNose_radius());
                    json.put("shrinkNose", data.getShrinkNose());
                    json.put("chin_radius", data.getChin_radius());
                    json.put("chin", data.getChin());
                    json.put("mouth_radius", data.getMouth_radius());
                    json.put("mouth", data.getMouth());
                    json.put("forehead_radius", data.getForehead_radius());
                    json.put("forehead", data.getForehead());
                    json.put("cheekbones_radius", data.getCheekbones_radius());
                    json.put("cheekbones", data.getCheekbones());
                    json.put("canthus_radius", data.getCanthus_radius());
                    json.put("canthus", data.getCanthus());
                    json.put("eyeSpan_radius", data.getEyeSpan_radius());
                    json.put("eyeSpan", data.getEyeSpan());
                    json.put("nosewing_radius", data.getNosewing_radius());
                    json.put("nosewing", data.getNosewing());
                    json.put("noseHeight_radius", data.getNoseHeight_radius());
                    json.put("noseHeight", data.getNoseHeight());
                    json.put("overallHeight_radius", data.getOverallHeight_radius());
                    json.put("overallHeight", data.getOverallHeight());
                }
            }
            catch (Throwable t)
            {

            }
        }
        return json;
    }


    public static ShapeInfo DecodeShape(JSONObject json)
    {
        ShapeInfo out = null;
        if (json != null)
        {
            try
            {
                out = new ShapeInfo();
                out.setResType(BaseInfo.RES_TYPE_SYNC);
                out.setShowType(BaseInfo.SHOW_TYPE_ALL);
                if (json.has("type"))
                {
                    //0：镜头社区&直播助手共用，1；镜头社区，2：直播助手
                    int type = json.getInt("type");
                    if (type == 0)
                    {
                        out.setShowType(BaseInfo.SHOW_TYPE_ALL);
                    }
                    else if (type == 1)
                    {
                        out.setShowType(BaseInfo.SHOW_TYPE_CAMERA);
                    }
                    else if (type == 2)
                    {
                        out.setShowType(BaseInfo.SHOW_TYPE_LIVE);
                    }
                }
                ShapeData data = new ShapeData();
                data.setEyes_type(ShapeData.EYE_TYPE.OVAL_EYES);
                if (json.has("thinFace_radius"))
                {
                    data.setThinFace_radius((float) json.getDouble("thinFace_radius"));
                }
                if (json.has("thinFace"))
                {
                    data.setThinFace((float) json.getDouble("thinFace"));
                }
                if (json.has("littleFace_radius"))
                {
                    data.setLittleFace_radius((float) json.getDouble("littleFace_radius"));
                }
                if (json.has("littleFace"))
                {
                    data.setLittleFace((float) json.getDouble("littleFace"));
                }
                if (json.has("shavedFace_radius"))
                {
                    data.setShavedFace_radius((float) json.getDouble("shavedFace_radius"));
                }
                if (json.has("shavedFace"))
                {
                    data.setShavedFace((float) json.getDouble("shavedFace"));
                }
                if (json.has("bigEye_radius"))
                {
                    data.setBigEye_radius((float) json.getDouble("bigEye_radius"));
                }
                if (json.has("bigEye"))
                {
                    data.setBigEye((float) json.getDouble("bigEye"));
                }
                if (json.has("shrinkNose_radius"))
                {
                    data.setShrinkNose_radius((float) json.getDouble("shrinkNose_radius"));
                }
                if (json.has("shrinkNose"))
                {
                    data.setShrinkNose((float) json.getDouble("shrinkNose"));
                }
                if (json.has("chin_radius"))
                {
                    data.setChin_radius((float) json.getDouble("chin_radius"));
                }
                if (json.has("chin"))
                {
                    data.setChin((float) json.getDouble("chin"));
                }
                if (json.has("mouth_radius"))
                {
                    data.setMouth_radius((float) json.getDouble("mouth_radius"));
                }
                if (json.has("mouth"))
                {
                    data.setMouth((float) json.getDouble("mouth"));
                }
                if (json.has("forehead_radius"))
                {
                    data.setForehead_radius((float) json.getDouble("forehead_radius"));
                }
                if (json.has("forehead"))
                {
                    data.setForehead((float) json.getDouble("forehead"));
                }
                if (json.has("cheekbones_radius"))
                {
                    data.setCheekbones_radius((float) json.getDouble("cheekbones_radius"));
                }
                if (json.has("cheekbones"))
                {
                    data.setCheekbones((float) json.getDouble("cheekbones"));
                }
                if (json.has("canthus_radius"))
                {
                    data.setCanthus_radius((float) json.getDouble("canthus_radius"));
                }
                if (json.has("canthus"))
                {
                    data.setCanthus((float) json.getDouble("canthus"));
                }
                if (json.has("eyeSpan_radius"))
                {
                    data.setEyeSpan_radius((float) json.getDouble("eyeSpan_radius"));
                }
                if (json.has("eyeSpan"))
                {
                    data.setEyeSpan((float) json.getDouble("eyeSpan"));
                }
                if (json.has("nosewing_radius"))
                {
                    data.setNosewing_radius((float) json.getDouble("nosewing_radius"));
                }
                if (json.has("nosewing"))
                {
                    data.setNosewing((float) json.getDouble("nosewing"));
                }
                if (json.has("noseHeight_radius"))
                {
                    data.setNoseHeight_radius((float) json.getDouble("noseHeight_radius"));
                }
                if (json.has("noseHeight"))
                {
                    data.setNoseHeight((float) json.getDouble("noseHeight"));
                }
                if (json.has("overallHeight_radius"))
                {
                    data.setOverallHeight_radius((float) json.getDouble("overallHeight_radius"));
                }
                if (json.has("overallHeight"))
                {
                    data.setOverallHeight((float) json.getDouble("overallHeight"));
                }
                out.setData(data);
            }
            catch (Throwable t)
            {
                t.printStackTrace();
                out = null;
            }
        }
        return out;
    }
}
