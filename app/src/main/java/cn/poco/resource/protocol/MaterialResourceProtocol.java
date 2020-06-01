package cn.poco.resource.protocol;

import android.content.Context;
import android.util.Base64;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.NetCore2;

/**
 * 素材开放平台请求协议
 *
 * @author lmx
 *         Created by lmx on 2017/7/20.
 */

public class MaterialResourceProtocol
{
    //注意：服务器定义请求版本号
    public static final String MATERIAL_RESOURCE_SERVER_VERSION = "3.0.0";

    //注意：项目名
    public static final String PROJECT_NAME = "beauty_camera";

    //注意：必须在请求素材资源前初始化
    public static boolean IS_DEBUG = false;


    /**
     * @param url          请求url（注意测试、正式环境）
     * @param server_ver   服务器版本 {@link #MATERIAL_RESOURCE_SERVER_VERSION}
     * @param isEnc        是否加密
     * @param mKey         手机imei
     * @param paramJson    param请求数据（详细看api文档） {@link #GetReqParams(PageType, boolean, ResourceGroup[])}
     * @param comeFromJson come_from请求数据（详细看api文档）{@link #GetReqComeFromParams(Context)}
     * @param headerParams 头部请求数据
     * @return
     */
    public static byte[] Get(String url, String server_ver, boolean isEnc, String mKey, JSONObject paramJson, JSONObject comeFromJson, HashMap<String, String> headerParams)
    {
        byte[] out = null;

        StringBuilder buf = new StringBuilder(256);
        buf.append(url);
        if (url.contains("?"))
        {
            buf.append('&');
        }
        else
        {
            buf.append('?');
        }
        buf.append("req=");
        String jsonString = MakeProtocolJson(server_ver, isEnc, mKey, paramJson, comeFromJson);
        buf.append(new String(Base64.encode(jsonString.getBytes(), Base64.NO_WRAP | Base64.URL_SAFE)));
        NetCore2 net = null;
        try
        {
            net = new NetCore2();
            NetCore2.NetMsg msg = net.HttpGet(buf.toString(), headerParams);
            if (msg != null && msg.m_stateCode == HttpURLConnection.HTTP_OK && msg.m_data != null)
            {
                out = msg.m_data;
            }
        }
        catch (Throwable ignored)
        {

        }
        finally
        {
            if (net != null)
            {
                net.ClearAll();
                net = null;
            }
        }

        return out;
    }


    /**
     * @param url          请求url（注意测试、正式环境）
     * @param server_ver   服务器版本 {@link #MATERIAL_RESOURCE_SERVER_VERSION}
     * @param isEnc        是否加密
     * @param mKey         手机imei
     * @param paramJson    param请求数据（详细看api文档）
     * @param comeFromJson come_from请求数据（详细看api文档）
     * @param params       表单参数,可为null
     * @param data         表单文件数据(路径 / 文件字节数据),可为null
     * @return
     */
    public static byte[] Post(String url, String server_ver, boolean isEnc, String mKey, JSONObject paramJson, JSONObject comeFromJson, HashMap<String, String> params, List<NetCore2.FormData> data)
    {
        byte[] out = null;

        String jsonString = MakeProtocolJson(server_ver, isEnc, mKey, paramJson, comeFromJson);
        if (params == null)
        {
            params = new HashMap<>();
        }
        params.put("req", URLEncoder.encode(jsonString));
        NetCore2 net = null;
        try
        {
            net = new NetCore2();
            NetCore2.NetMsg msg = net.HttpPost(url, params, data);
            if (msg != null && msg.m_stateCode == HttpURLConnection.HTTP_OK && msg.m_data != null)
            {
                out = msg.m_data;
            }
        }
        catch (Throwable ignored)
        {

        }
        finally
        {
            if (net != null)
            {
                net.ClearAll();
                net = null;
            }
        }

        return out;
    }


    /**
     * 生成请求params（注意：come_from 请求param必须放在第一位）
     *
     * @param server_ver 服务器版本
     * @param isEnc      是否加密
     * @param mKey       手机imei
     * @param paramJson  数据
     */
    public static String MakeProtocolJson(String server_ver, boolean isEnc, String mKey, JSONObject paramJson, JSONObject comeFromJson)
    {
        JSONObject postJson = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder();
        try
        {
            if (paramJson == null)
            {
                paramJson = new JSONObject();
            }

            if (comeFromJson == null)
            {
                comeFromJson = new JSONObject();
            }

            String param = new StringBuilder().append("poco_").append(paramJson.toString()).append("_app").toString();
            String signStr = CommonUtils.Encrypt("MD5", param);
            String signCode = signStr.substring(5, (signStr.length() - 8));

//            postJson.put("come_from", comeFromJson);
            String comeFromStr = "\"come_from\":" + comeFromJson.toString() + ",";

            postJson.put("version", server_ver);
            postJson.put("os_type", "android");
            postJson.put("ctime", System.currentTimeMillis());
            postJson.put("app_name", "material_platform_android");//网站定义app_name
            if (isEnc)
            {
                postJson.put("is_enc", 1);
            }
            else
            {
                postJson.put("is_enc", 0);
            }
            if (mKey != null)
            {
                postJson.put("imei", mKey);
            }
            postJson.put("sign_code", signCode);
            postJson.put("param", paramJson);

            stringBuilder.append(postJson.toString());
            stringBuilder.insert(1, comeFromStr);
            postJson.put("come_from", comeFromJson);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 1、app_name: APP客户端beauty_camera_android<br/>
     * 2、version：APP当前版本号<br/>
     * 3、project_name：当前APP项目名
     *
     * @param context
     * @return
     */
    public static JSONObject GetReqComeFromParams(Context context)
    {
        JSONObject json = new JSONObject();
        try
        {
            json.put("app_name", ParamsInterface.GetInstance().GetAppName(context));
            json.put("version", ParamsInterface.GetInstance().GetAppVersion(context));
            json.put("project_name", ParamsInterface.GetInstance().GetProjectName());
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * @param pageType       素材分类id{@link PageType}
     * @param isDebug        是否调试模式（88.8）
     * @param resourceGroups 请求下载数据类型模式{@link ResourceGroup}
     * @return
     */
    public static JSONObject GetReqParams(PageType pageType, boolean isDebug, ResourceGroup[] resourceGroups)
    {
        JSONObject json = new JSONObject();
        try
        {
            json.put("is_beta", ParamsInterface.GetInstance().IsBeta(isDebug));
            json.put("page_type", ParamsInterface.GetInstance().GetPageType(pageType));
            json.put("group", ParamsInterface.GetInstance().GetResourceGroup(resourceGroups));
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        return json;
    }

}
