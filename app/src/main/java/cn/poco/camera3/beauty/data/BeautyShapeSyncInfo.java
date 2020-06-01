package cn.poco.camera3.beauty.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.poco.pocointerfacelibs.AbsBaseInfo;

/**
 * @author Administrator
 *         Created by Administrator on 2018-01-19.
 */

public class BeautyShapeSyncInfo extends AbsBaseInfo
{
    public ArrayList<ShapeInfo> shapeInfos = new ArrayList<>();

    //app 添加参数的时间（单位：秒）
    public int get_info_app_time;

    //版本
    public int version;

    @Override
    protected boolean DecodeMyData(Object object) throws Throwable
    {
        JSONObject jsonObject = (JSONObject) object;
        if (jsonObject != null && jsonObject.has("ret_data"))
        {
            JSONObject ret_data = jsonObject.getJSONObject("ret_data");
            if (ret_data != null && ret_data.length() > 0)
            {
                if (ret_data.has("app_time"))
                {
                    this.get_info_app_time = ret_data.getInt("app_time");
                }

                if (ret_data.has("custom_data"))
                {
                    String custom_data = ret_data.getString("custom_data");
                    decodeCustomData(new JSONObject(custom_data));
                }
            }
        }

        return true;
    }

    private void decodeCustomData(JSONObject jsonObject)
    {
        if (jsonObject != null)
        {
            try
            {
                if (jsonObject.has("version"))
                {
                    this.version = jsonObject.getInt("version");
                }
                if (jsonObject.has("shape"))
                {
                    JSONArray jsonArr = jsonObject.getJSONArray("shape");
                    if (jsonArr != null && jsonArr.length() > 0)
                    {
                        int length = jsonArr.length();
                        for (int i = 0; i < length; i++)
                        {
                            ShapeInfo shapeInfo = ShapeSyncResMgr.DecodeShape(jsonArr.getJSONObject(i));
                            if (shapeInfo != null)
                            {
                                shapeInfos.add(shapeInfo);
                            }
                        }
                    }
                }
            }
            catch (Throwable t)
            {
                t.printStackTrace();
            }
        }
    }

}
