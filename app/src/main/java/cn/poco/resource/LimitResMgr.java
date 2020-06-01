package cn.poco.resource;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.cloudalbumlibs.utils.NetWorkUtils;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.NetCore2;

public class LimitResMgr{

//    public static final String VIDEO_FACE_REAL_LIMIT_URL = "http://img-wifi.poco.cn/mypoco/mtmpfile/API/beauty_facechat/get_real_limit.php";
    public static ArrayList<LimitRes> m_videoFaceLimitArr = null;


    public interface CheckRealLimitResCallback {
        void onProgress();

        void onFinish(int resultCode);
    }

    /**
     * 限量素材
     *
     * @param data
     * @return
     */
    public static ArrayList<LimitRes> GetVideoFaceLimitArr(String data) {
        ArrayList<LimitRes> out = new ArrayList<>();
        if (data != null) {
            try {
                JSONArray jsonArr = new JSONArray(data);
                int arrLen = jsonArr.length();
                JSONObject jsonObj;
                String temp;
                LimitRes info;
                for (int i = 0; i < arrLen; i++) {
                    info = null;
                    jsonObj = jsonArr.getJSONObject(i);
                    if (jsonObj.has("is_limit")) {
                        temp = jsonObj.getString("is_limit");
                        if (temp != null) {
                            if (temp.equals("1")) {
                                info = new LimitRes();
                                info.isLimit = true;
                            }
                            if (info != null) {
                                info.m_type = BaseRes.TYPE_NETWORK_URL;
                                try {
                                    String id = jsonObj.getString("id");
                                    if (!TextUtils.isEmpty(id)) {
                                        info.m_id = Integer.parseInt(id);
                                    }
                                    if (jsonObj.has("limitEnd")) {
                                        info.isLimitEnd = jsonObj.getInt("limitEnd") == 0 ? false : true;
                                    }
                                    if (jsonObj.has("real_limit")) {
                                        info.mRealLimit = jsonObj.getString("real_limit");
                                    }
                                    if (jsonObj.has("limitType")) {
                                        info.mLimitType = jsonObj.getString("limitType");
                                    }
                                    if (jsonObj.has("limitExplainRemainingThumb")) {
                                        info.mLimitExplainRemainingThumbUrl = jsonObj.getString("limitExplainRemainingThumb");
                                    }
                                    if (jsonObj.has("limitThumb")) {
                                        info.mLimitThumbUrl = jsonObj.getString("limitThumb");
                                    }
                                    if (jsonObj.has("limitExplainThumb")) {
                                        info.mLimitExplainThumbUrl = jsonObj.getString("limitExplainThumb");
                                    }
                                    if (jsonObj.has("limitExplainTitle")) {
                                        info.mLimitExplainTitle = jsonObj.getString("limitExplainTitle");
                                    }
                                    if (jsonObj.has("limitExplainContent")) {
                                        info.mLimitExplainContent = jsonObj.getString("limitExplainContent");
                                    }
                                    if (jsonObj.has("limitExplainEndText")) {
                                        info.mLimitExplainEndText = jsonObj.getString("limitExplainEndText");
                                    }
                                    if (jsonObj.has("limitExplainEndTextLink")) {
                                        info.mLimitExplainEndTextLink = jsonObj.getString("limitExplainEndTextLink");
                                    }
                                    if (jsonObj.has("limitExplainEndGoWebButtonText")) {
                                        info.mLimitExplainEndGoWebButtonText = jsonObj.getString("limitExplainEndGoWebButtonText");
                                    }
                                    if (jsonObj.has("flight_key")) {
                                        info.mFlightKey = jsonObj.getString("flight_key");
                                    }
                                    out.add(info);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return out;
    }

    /**
     * 下载时，检查限量个数
     * @param context
     * @param appVer
     * @param limitRes
     * @param callback
     */
    public static void checkVideoFaceRealLimit(final Context context, final String appVer, final LimitRes limitRes, final CheckRealLimitResCallback callback) {
        if (limitRes == null || TextUtils.isEmpty(limitRes.mFlightKey)) {
            if (callback != null) {
                callback.onFinish(0);
            }
            return;
        }
        if (limitRes.isLimitEnd) {
            if (callback != null) {
                callback.onFinish(2);
            }
            return;
        }
        if (!NetWorkUtils.isNetworkConnected(context)) {
            if (callback != null) {
                callback.onFinish(1);
            }
            return;
        }
        if (callback != null) {
            callback.onProgress();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject postJson = new JSONObject();
                    postJson.put("flight_key", limitRes.mFlightKey);
                    postJson.put("limitType", limitRes.mLimitType);
                    final String data = null;//getServiceJson(VIDEO_FACE_REAL_LIMIT_URL, appVer, postJson);
                    if (data != null) {
//                        Log.i("bbb", "data:"+data);
                        JSONObject jsonObject = new JSONObject(data);
                        if (jsonObject != null) {
                            if (jsonObject.has("limitEnd")) {
                                limitRes.isLimitEnd = jsonObject.getString("limitEnd").equals("0") ? false : true;
                            }
                            if (jsonObject.has("real_limit")) {
                                limitRes.mRealLimit = jsonObject.getString("real_limit");
                            }
                            if (jsonObject.has("limitExplainRemainingThumb")) {
                                limitRes.mLimitExplainRemainingThumbUrl = jsonObject.getString("limitExplainRemainingThumb");

                                if (!TextUtils.isEmpty(limitRes.mLimitExplainRemainingThumbUrl)
                                        && !limitRes.mLimitExplainRemainingThumbUrl.contains("?limit_icon_v2=1")) {//新版图标标记
                                    limitRes.mLimitExplainRemainingThumbUrl += "?limit_icon_v2=1";
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (callback != null) {
                    callback.onFinish(2);
                }
            }
        }).start();
    }

    protected static String getServiceJson(String url, final String appVer, JSONObject paramJson) throws Exception {
        String result = null;
        String jsonString = null;
        JSONObject postJson = new JSONObject();
        if (paramJson == null) {
            paramJson = new JSONObject();
        }
        String param = new StringBuilder().append("poco_").append(paramJson.toString()).append("_app").toString();
        String signStr = CommonUtils.Encrypt("MD5", param);
        String signCode = signStr.substring(5, (signStr.length() - 8));

        postJson.put("version", appVer);
        postJson.put("os_type", "android");
        postJson.put("ctime", System.currentTimeMillis());
        postJson.put("app_name", "facechat");
        postJson.put("is_enc", 0);
        postJson.put("sign_code", signCode);
        postJson.put("param", paramJson);

        jsonString = postJson.toString();
        NetCore2 net = new NetCore2();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("req", jsonString);
        NetCore2.NetMsg msg = net.HttpPost(url, map, null);
        if (msg != null && msg.m_stateCode == HttpURLConnection.HTTP_OK && msg.m_data != null) {
            result = new String(msg.m_data);
        }
        if (net != null) {
            net.ClearAll();
        }
        return result;
    }
}
