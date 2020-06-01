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
 *         Created by lmx on 2018-01-16.
 */
public class ShapeResMgr extends BaseShapeResMgr<ShapeInfo, ArrayList<ShapeInfo>>
{
    //TODO json结构版本号
    public static final int NEW_JSON_VERSION = 8;

    protected final String SDCARD_PATH = DownloadMgr.getInstance().SHAPE_DATA_PATH + "/shape_data.xxxx";

    private ShapeResMgr()
    {
    }

    public static ShapeResMgr sInstance;

    public static synchronized ShapeResMgr getInstance()
    {
        if (sInstance == null)
        {
            sInstance = new ShapeResMgr();
        }
        return sInstance;
    }

    @Override
    public int GetResInfoArrSize(ArrayList<ShapeInfo> arr)
    {
        return arr == null ? 0 : arr.size();
    }

    @Override
    protected ArrayList<ShapeInfo> GetLocalInfoRes(Context context)
    {
        //NOTE 目前镜头社区和直播助手的脸型数据共用
        //TODO 初始化预设内置数据
        ArrayList<ShapeInfo> out = MakeResInfoArrObj();

        ArrayList<ShapeInfo> live = GetLocalInfoResLive(context);
        if (live != null && live.size() > 0)
        {
            out.addAll(live);
        }

        ArrayList<ShapeInfo> camera = GetLocalInfoResCamera(context);
        if (camera != null && camera.size() > 0)
        {
            out.addAll(camera);
        }

        //无脸型，脸型参数均为0
        ShapeInfo shapeInfo = new ShapeInfo();
        shapeInfo.setId(SuperShapeData.ID_NON_SHAPE);
        shapeInfo.setResType(BaseInfo.RES_TYPE_LOCAL);
        shapeInfo.setShowType(BaseInfo.SHOW_TYPE_ALL);
        shapeInfo.setName("无");
        shapeInfo.setParamsData(SuperShapeData.GetNonShape());
        shapeInfo.getData().setNone(true);
        out.add(0, shapeInfo);

        return out;
    }

    @Override
    protected ArrayList<ShapeInfo> GetLocalInfoResLive(Context context)
    {
        // TODO 初始化预设内置脸型数据（直播助手）
        return null;
    }

    @Override
    protected ArrayList<ShapeInfo> GetLocalInfoResCamera(Context context)
    {
        // TODO 初始化预设内置脸型数据（镜头社区）
        ArrayList<ShapeInfo> out = MakeResInfoArrObj();

        ShapeInfo shapeInfo;

        //自然修饰（椭眼）
        shapeInfo = new ShapeInfo();
        shapeInfo.setId(SuperShapeData.ID_ZIRANXIUSHI);
        shapeInfo.setResType(BaseInfo.RES_TYPE_LOCAL);
        shapeInfo.setShowType(BaseInfo.SHOW_TYPE_CAMERA);
        shapeInfo.setName("自然修饰");
        shapeInfo.setParamsData(SuperShapeData.GetZIranxiushi());
        shapeInfo.getData().setEyes_type(ShapeData.EYE_TYPE.OVAL_EYES);
        out.add(shapeInfo);

        //芭比公主（椭眼）
        shapeInfo = new ShapeInfo();
        shapeInfo.setId(SuperShapeData.ID_BABIGONGZHU);
        shapeInfo.setResType(BaseInfo.RES_TYPE_LOCAL);
        shapeInfo.setShowType(BaseInfo.SHOW_TYPE_CAMERA);
        shapeInfo.setName("芭比公主");
        shapeInfo.setParamsData(SuperShapeData.GetBabigongzhu());
        shapeInfo.getData().setEyes_type(ShapeData.EYE_TYPE.OVAL_EYES);
        out.add(shapeInfo);

        //精致网红（椭眼）
        shapeInfo = new ShapeInfo();
        shapeInfo.setId(SuperShapeData.ID_JINGZHIWANGHONG);
        shapeInfo.setResType(BaseInfo.RES_TYPE_LOCAL);
        shapeInfo.setShowType(BaseInfo.SHOW_TYPE_CAMERA);
        shapeInfo.setName("精致网红");
        shapeInfo.setParamsData(SuperShapeData.GetJingzhiWanghong());
        shapeInfo.getData().setEyes_type(ShapeData.EYE_TYPE.OVAL_EYES);
        out.add(shapeInfo);

        //激萌少女（椭眼）
        shapeInfo = new ShapeInfo();
        shapeInfo.setId(SuperShapeData.ID_JIMENGSHAONV);
        shapeInfo.setResType(BaseInfo.RES_TYPE_LOCAL);
        shapeInfo.setShowType(BaseInfo.SHOW_TYPE_CAMERA);
        shapeInfo.setName("激萌少女");
        shapeInfo.setParamsData(SuperShapeData.GetJimengshaonv());
        shapeInfo.getData().setEyes_type(ShapeData.EYE_TYPE.OVAL_EYES);
        out.add(shapeInfo);

        //摩登女王（椭眼）
        shapeInfo = new ShapeInfo();
        shapeInfo.setId(SuperShapeData.ID_MODENGNVWANG);
        shapeInfo.setResType(BaseInfo.RES_TYPE_LOCAL);
        shapeInfo.setShowType(BaseInfo.SHOW_TYPE_CAMERA);
        shapeInfo.setName("摩登女王");
        shapeInfo.setParamsData(SuperShapeData.GetModengnvwang());
        shapeInfo.getData().setEyes_type(ShapeData.EYE_TYPE.OVAL_EYES);
        out.add(shapeInfo);

        //呆萌甜心（椭眼）
        shapeInfo = new ShapeInfo();
        shapeInfo.setId(SuperShapeData.ID_DAIMENGTIANXIN);
        shapeInfo.setResType(BaseInfo.RES_TYPE_LOCAL);
        shapeInfo.setShowType(BaseInfo.SHOW_TYPE_CAMERA);
        shapeInfo.setName("呆萌甜心");
        shapeInfo.setParamsData(SuperShapeData.GetDamengtianxin());
        shapeInfo.getData().setEyes_type(ShapeData.EYE_TYPE.OVAL_EYES);
        out.add(shapeInfo);

        //嘟嘟童颜（圆眼）
        shapeInfo = new ShapeInfo();
        shapeInfo.setId(SuperShapeData.ID_DUDUTONGYAN);
        shapeInfo.setResType(BaseInfo.RES_TYPE_LOCAL);
        shapeInfo.setShowType(BaseInfo.SHOW_TYPE_CAMERA);
        shapeInfo.setName("嘟嘟童颜");
        shapeInfo.setParamsData(SuperShapeData.GetDudulianton());
        shapeInfo.getData().setEyes_type(ShapeData.EYE_TYPE.CIRCLE_EYES);
        out.add(shapeInfo);

        //小脸女神（椭眼）
        shapeInfo = new ShapeInfo();
        shapeInfo.setId(SuperShapeData.ID_XIAOLIANNVSHEN);
        shapeInfo.setResType(BaseInfo.RES_TYPE_LOCAL);
        shapeInfo.setShowType(BaseInfo.SHOW_TYPE_CAMERA);
        shapeInfo.setName("小脸女神");
        shapeInfo.setParamsData(SuperShapeData.GetXiaoliannvshen());
        shapeInfo.getData().setEyes_type(ShapeData.EYE_TYPE.OVAL_EYES);
        out.add(shapeInfo);

        return out;
    }

    @Override
    public ArrayList<ShapeInfo> MakeResInfoArrObj()
    {
        return new ArrayList<>();
    }

    @Override
    public boolean ResInfoArrAddItem(ArrayList<ShapeInfo> arr, ShapeInfo item)
    {
        return arr.add(item);
    }

    @Override
    protected ArrayList<ShapeInfo> CheckSdcardInfoRes(Context context, @Nullable ArrayList<ShapeInfo> arr)
    {
        //判断系统数据是否被清除，需要是否恢复初始化数据
        boolean isNew = TagMgr.CheckTag(context, Tags.RESOURCE_SHAPE_VERSION);
        int vc = TagMgr.GetTagIntValue(context, Tags.RESOURCE_SHAPE_VERSION);
        if (isNew)
        {
            FileUtil.deleteSDFile(GetSdcardPath(context));
            arr = null;
        }
        TagMgr.SetTagValue(context, Tags.RESOURCE_SHAPE_VERSION, String.valueOf(CommonUtils.GetAppVerCode(context)));

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
        return ReadResourceItem(jsonObject, isPath);
    }

    public static final ShapeInfo ReadResourceItem(JSONObject jsonObject, boolean isPath)
    {
        ShapeInfo out = null;
        if (jsonObject != null)
        {
            try
            {
                ShapeData data = new ShapeData();
                out = new ShapeInfo();
                if (jsonObject.has("id"))
                {
                    out.setId(jsonObject.getInt("id"));
                }
                if (jsonObject.has("name"))
                {
                    out.setName(jsonObject.getString("name"));
                }
                if (jsonObject.has("update_time"))
                {
                    out.setUpdate_time(jsonObject.getLong("update_time"));
                }
                if (jsonObject.has("is_need_synchronize"))
                {
                    out.setNeedSynchronize(jsonObject.getBoolean("is_need_synchronize"));
                }
                if (jsonObject.has("is_modify"))
                {
                    out.setModify(jsonObject.getBoolean("is_modify"));
                }
                if (jsonObject.has("is_default_data"))
                {
                    out.setDefaultData(jsonObject.getBoolean("is_default_data"));
                }
                if (jsonObject.has("resType"))
                {
                    out.setResType(jsonObject.getInt("resType"));
                }
                if (jsonObject.has("showType"))
                {
                    out.setShowType(jsonObject.getInt("showType"));
                }
                if (jsonObject.has("is_none"))
                {
                    data.setNone(jsonObject.getBoolean("is_none"));
                }
                if (jsonObject.has("eyes_type"))
                {
                    data.setEyes_type(jsonObject.getInt("eyes_type"));
                }
                if (jsonObject.has("bigEye"))
                {
                    data.setBigEye((float) jsonObject.getDouble("bigEye"));
                }
                if (jsonObject.has("bigEye_radius"))
                {
                    data.setBigEye_radius((float) jsonObject.getDouble("bigEye_radius"));
                }
                if (jsonObject.has("chin"))
                {
                    data.setChin((float) jsonObject.getDouble("chin"));
                }
                if (jsonObject.has("chin_radius"))
                {
                    data.setChin_radius((float) jsonObject.getDouble("chin_radius"));
                }
                if (jsonObject.has("littleFace"))
                {
                    data.setLittleFace((float) jsonObject.getDouble("littleFace"));
                }
                if (jsonObject.has("littleFace_radius"))
                {
                    data.setLittleFace_radius((float) jsonObject.getDouble("littleFace_radius"));
                }
                if (jsonObject.has("mouth"))
                {
                    data.setMouth((float) jsonObject.getDouble("mouth"));
                }
                if (jsonObject.has("mouth_radius"))
                {
                    data.setMouth_radius((float) jsonObject.getDouble("mouth_radius"));
                }
                if (jsonObject.has("shavedFace"))
                {
                    data.setShavedFace((float) jsonObject.getDouble("shavedFace"));
                }
                if (jsonObject.has("shavedFace_radius"))
                {
                    data.setShavedFace_radius((float) jsonObject.getDouble("shavedFace_radius"));
                }
                if (jsonObject.has("shrinkNose"))
                {
                    data.setShrinkNose((float) jsonObject.getDouble("shrinkNose"));
                }
                if (jsonObject.has("shrinkNose_radius"))
                {
                    data.setShrinkNose_radius((float) jsonObject.getDouble("shrinkNose_radius"));
                }
                if (jsonObject.has("thinFace"))
                {
                    data.setThinFace((float) jsonObject.getDouble("thinFace"));
                }
                if (jsonObject.has("thinFace_radius"))
                {
                    data.setThinFace_radius((float) jsonObject.getDouble("thinFace_radius"));
                }
                if (jsonObject.has("forehead_radius"))
                {
                    data.setForehead_radius((float) jsonObject.getDouble("forehead_radius"));
                }
                if (jsonObject.has("forehead"))
                {
                    data.setForehead((float) jsonObject.getDouble("forehead"));
                }
                if (jsonObject.has("cheekbones_radius"))
                {
                    data.setCheekbones_radius((float) jsonObject.getDouble("cheekbones_radius"));
                }
                if (jsonObject.has("cheekbones"))
                {
                    data.setCheekbones((float) jsonObject.getDouble("cheekbones"));
                }
                if (jsonObject.has("canthus_radius"))
                {
                    data.setCanthus_radius((float) jsonObject.getDouble("canthus_radius"));
                }
                if (jsonObject.has("canthus"))
                {
                    data.setCanthus((float) jsonObject.getDouble("canthus"));
                }
                if (jsonObject.has("eyeSpan_radius"))
                {
                    data.setEyeSpan_radius((float) jsonObject.getDouble("eyeSpan_radius"));
                }
                if (jsonObject.has("eyeSpan"))
                {
                    data.setEyeSpan((float) jsonObject.getDouble("eyeSpan"));
                }
                if (jsonObject.has("nosewing_radius"))
                {
                    data.setNosewing_radius((float) jsonObject.getDouble("nosewing_radius"));
                }
                if (jsonObject.has("nosewing"))
                {
                    data.setNosewing((float) jsonObject.getDouble("nosewing"));
                }
                if (jsonObject.has("noseHeight_radius"))
                {
                    data.setNoseHeight_radius((float) jsonObject.getDouble("noseHeight_radius"));
                }
                if (jsonObject.has("noseHeight"))
                {
                    data.setNoseHeight((float) jsonObject.getDouble("noseHeight"));
                }
                if (jsonObject.has("overallHeight_radius"))
                {
                    data.setOverallHeight_radius((float) jsonObject.getDouble("overallHeight_radius"));
                }
                if (jsonObject.has("overallHeight"))
                {
                    data.setOverallHeight((float) jsonObject.getDouble("overallHeight"));
                }
                if (jsonObject.has("smile_radius"))
                {
                    data.setSmile_radius((float) jsonObject.getDouble("smile_radius"));
                }
                if (jsonObject.has("smile"))
                {
                    data.setSmile((float) jsonObject.getDouble("smile"));
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
    protected boolean SaveSdcardRes(Context context, ArrayList<ShapeInfo> arr)
    {
        return SaveSdcardResource(context, GetSdcardPath(context), arr, NEW_JSON_VERSION);
    }

    public static final boolean SaveSdcardResource(@Nullable Context context, String sdcardPath, ArrayList<ShapeInfo> arr, int new_json_version)
    {
        boolean result = false;
        FileOutputStream fos = null;
        try
        {
            JSONObject json = new JSONObject();
            json.put("version", new_json_version);
            JSONArray jsonArr = new JSONArray();
            if (arr != null)
            {
                for (ShapeInfo info : arr)
                {
                    JSONObject resJson = new JSONObject();
                    resJson.put("id", info.getId());
                    resJson.put("name", info.getName() == null ? "" : info.getName());
                    resJson.put("resType", info.getResType());
                    resJson.put("showType", info.getShowType());
                    resJson.put("update_time", info.getUpdate_time());
                    resJson.put("is_need_synchronize", info.isNeedSynchronize());
                    resJson.put("is_modify", info.isModify());
                    resJson.put("is_default_data", info.isDefaultData());
                    resJson.put("is_none", info.getData().isNone());

                    resJson.put("eyes_type", info.getData().getEyes_type());
                    resJson.put("bigEye", info.getData().getBigEye());
                    resJson.put("bigEye_radius", info.getData().getBigEye_radius());
                    resJson.put("chin", info.getData().getChin());
                    resJson.put("chin_radius", info.getData().getChin_radius());
                    resJson.put("littleFace", info.getData().getLittleFace());
                    resJson.put("littleFace_radius", info.getData().getLittleFace_radius());
                    resJson.put("mouth", info.getData().getMouth());
                    resJson.put("mouth_radius", info.getData().getMouth_radius());
                    resJson.put("shavedFace", info.getData().getShavedFace());
                    resJson.put("shavedFace_radius", info.getData().getShavedFace_radius());
                    resJson.put("shrinkNose", info.getData().getShrinkNose());
                    resJson.put("shrinkNose_radius", info.getData().getShrinkNose_radius());
                    resJson.put("thinFace", info.getData().getThinFace());
                    resJson.put("thinFace_radius", info.getData().getThinFace_radius());
                    resJson.put("forehead_radius", info.getData().getForehead_radius());
                    resJson.put("forehead", info.getData().getForehead());
                    resJson.put("cheekbones_radius", info.getData().getCheekbones_radius());
                    resJson.put("cheekbones", info.getData().getCheekbones());
                    resJson.put("canthus_radius", info.getData().getCanthus_radius());
                    resJson.put("canthus", info.getData().getCanthus());
                    resJson.put("eyeSpan_radius", info.getData().getEyeSpan_radius());
                    resJson.put("eyeSpan", info.getData().getEyeSpan());
                    resJson.put("nosewing_radius", info.getData().getNosewing_radius());
                    resJson.put("nosewing", info.getData().getNosewing());
                    resJson.put("noseHeight_radius", info.getData().getNoseHeight_radius());
                    resJson.put("noseHeight", info.getData().getNoseHeight());
                    resJson.put("overallHeight_radius", info.getData().getOverallHeight_radius());
                    resJson.put("overallHeight", info.getData().getOverallHeight());
                    resJson.put("smile_radius", info.getData().getSmile_radius());
                    resJson.put("smile", info.getData().getSmile());
                    jsonArr.put(resJson);
                }
            }
            json.put("data", jsonArr);
            fos = new FileOutputStream(sdcardPath);
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
        return result;
    }

}
