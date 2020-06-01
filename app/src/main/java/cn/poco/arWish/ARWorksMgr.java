package cn.poco.arWish;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import cn.poco.pocointerfacelibs.IPOCO;
import cn.poco.protocol.PocoProtocol;
import cn.poco.resource.AbsDownloadMgr;
import cn.poco.resource.ArRes;
import cn.poco.resource.DownloadMgr;
import cn.poco.resource.IDownload;
import cn.poco.system.AppInterface;
import cn.poco.system.SysConfig;
import cn.poco.utils.FileUtil;

/**
 * 管理
 * Created by Gxx on 2018/1/27.
 */

public class ARWorksMgr
{
    public interface CallBack
    {
        void onLoadSucceed(ArRes res);

        void onLoadFailed(String error_zh, String error_en);

        void onDownloadFailed();

        void onNetError();
    }

    interface Msg
    {
        int GET_WORK_START = 1;
        int GET_WORK_SUCCEED = 2;
        int GET_WORK_FAILED = 3;
        int GET_WORK_NULL = 4;
    }

    private final String TEST_URL = "http://tw.adnonstop.com/zt/web/index.php?r=api/v1/appdata/ar-detail"; // 内测版ap
    private final String URL = "http://zt.adnonstop.com/index.php?r=api/v1/appdata/ar-detail"; //正式版api

    private HandlerThread mHandlerThread;

    private Handler mMainHandler;

    private Handler mPasswordReqHandler;

    private DownloadMgr.Callback mDownLoadThumbCB;

    private volatile CallBack mCB;

    private volatile boolean mHasStartLoadData;

    public ARWorksMgr()
    {
        init();
    }

    private void init()
    {
        mDownLoadThumbCB = new AbsDownloadMgr.Callback()
        {
            @Override
            public void OnProgress(int downloadId, IDownload res, int progress) {}

            @Override
            public void OnComplete(int downloadId, IDownload res)
            {
                if (res != null && res instanceof ArRes)
                {
                    if (mCB != null)
                    {
                        mCB.onLoadSucceed((ArRes) res);
                        mCB = null;
                        mHasStartLoadData = false;
                    }
                }
            }

            @Override
            public void OnFail(int downloadId, IDownload res)
            {
                if (mCB != null)
                {
                    mCB.onDownloadFailed();
                    mCB = null;
                    mHasStartLoadData = false;
                }
            }
        };

        mHandlerThread = new HandlerThread("request_ar_works");
        mHandlerThread.start();

        mPasswordReqHandler = new Handler(mHandlerThread.getLooper(), new Handler.Callback()
        {
            @Override
            public boolean handleMessage(Message msg)
            {
                switch (msg.what)
                {
                    case Msg.GET_WORK_START:
                    {
                        if (msg.obj != null)
                        {
                            HashMap<String, Object> params = (HashMap<String, Object>) msg.obj;
                            String version = "";
                            if (params.containsKey("version"))
                            {
                                version = (String) params.get("version");
                            }

                            String appName = "";
                            if (params.containsKey("app_name"))
                            {
                                appName = (String) params.get("app_name");
                            }

                            String mKey = "";
                            if (params.containsKey("imei"))
                            {
                                mKey = (String) params.get("imei");
                            }

                            JSONObject jsonObject = new JSONObject();
                            if (params.containsKey("param"))
                            {
                                jsonObject = (JSONObject) params.get("param");
                            }

                            String url = "";
                            if (params.containsKey("url"))
                            {
                                url = (String) params.get("url");
                            }

                            byte[] data = PocoProtocol.Get(url, version, appName, false, mKey, jsonObject, null);

                            if (data != null && data.length > 0)
                            {
                                try
                                {
                                    JSONObject object = new JSONObject(new String(data));
                                    if (object.has("code"))
                                    {
                                        int code = (int) object.get("code");

                                        if (code == 200 && object.has("data"))
                                        {
                                            object = object.getJSONObject("data");

                                            if (object != null && object.has("ret_code"))
                                            {
                                                int ret_code = (int) object.get("ret_code");
                                                if (ret_code == 0) // succeed
                                                {
                                                    ArRes res = parse(object);
                                                    if (res != null && mMainHandler != null)
                                                    {
                                                        Message message = mMainHandler.obtainMessage();
                                                        message.what = Msg.GET_WORK_SUCCEED;
                                                        message.obj = res;
                                                        mMainHandler.sendMessage(message);
                                                    }
                                                }
                                                else if (mMainHandler != null)
                                                {
                                                    ErrorMsg error = parseErrorMsg(object);
                                                    Message message = mMainHandler.obtainMessage();
                                                    message.obj = error;
                                                    message.what = Msg.GET_WORK_FAILED;
                                                    mMainHandler.sendMessage(message);
                                                }
                                            }

                                        }
                                    }

                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            else if (mMainHandler != null)
                            {
                                mMainHandler.sendEmptyMessage(Msg.GET_WORK_NULL);
                            }
                        }
                        break;
                    }

                    default:
                        break;
                }
                return true;
            }
        });

        mMainHandler = new Handler(Looper.getMainLooper(), new Handler.Callback()
        {
            @Override
            public boolean handleMessage(Message msg)
            {
                switch (msg.what)
                {
                    case Msg.GET_WORK_SUCCEED:
                    {
                        if (msg.obj != null)
                        {
                            ArRes res = (ArRes) msg.obj;
                            String img_thumb_path = res.getImageThumbPath();
                            String path = res.GetSaveParentPath() + File.separator + res.m_show_id + File.separator + img_thumb_path;
                            if (FileUtil.isFileExists(path))
                            {
                                res.m_thumb = path;
                                res.m_avatar_path = res.GetSaveParentPath() + File.separator + res.m_show_id + File.separator + res.getAvatarThumbPath();
                                if (mCB != null)
                                {
                                    mCB.onLoadSucceed(res);
                                    mCB = null;
                                    mHasStartLoadData = false;
                                }
                            }
                            else
                            {
                                DownloadMgr.getInstance().DownloadRes(res, mDownLoadThumbCB);
                            }
                        }
                        break;
                    }

                    case Msg.GET_WORK_NULL:
                    {
                        if (mCB != null)
                        {
                            mCB.onNetError();
                        }
                        mHasStartLoadData = false;
                        break;
                    }

                    case Msg.GET_WORK_FAILED:
                    {
                        if (msg.obj != null)
                        {
                            ErrorMsg error = (ErrorMsg) msg.obj;
                            if (mCB != null)
                            {
                                mCB.onLoadFailed(error.mChinese, error.mEnglish);
                                mCB = null;
                                mHasStartLoadData = false;
                            }
                        }
                        break;
                    }

                    default:
                        break;
                }
                return true;
            }
        });
    }

    private ErrorMsg parseErrorMsg(JSONObject jsonObject)
    {
        ErrorMsg out = new ErrorMsg();

        if (jsonObject != null)
        {
            if (jsonObject.has("ret_data"))
            {
                try
                {
                    JSONObject object = jsonObject.getJSONObject("ret_data");

                    if (object != null)
                    {
                        if (object.has("notice_zh"))
                        {
                            out.mChinese = (String) object.get("notice_zh");
                        }

                        if (object.has("notice_en"))
                        {
                            out.mEnglish = (String) object.get("notice_en");
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        return out;
    }

    private ArRes parse(JSONObject jsonObject)
    {
        if (jsonObject == null) return null;

        ArRes out = new ArRes();

        if (jsonObject.has("ret_data"))
        {
            try
            {
                JSONObject object = jsonObject.getJSONObject("ret_data");

                if (object != null)
                {
                    if (object.has("show_id"))
                    {
                        out.m_show_id = (String) object.get("show_id");
                    }

                    if (object.has("user_id"))
                    {
                        out.m_user_id = (String) object.get("user_id");
                    }

                    if (object.has("nickname"))
                    {
                        out.m_user_name = (String) object.get("nickname");
                    }

                    if (object.has("img"))
                    {
                        out.m_img_url = (String) object.get("img");
                    }

                    if (object.has("video_url"))
                    {
                        out.m_video_url = (String) object.get("video_url");
                    }

                    if (object.has("avatar"))
                    {
                        out.m_avatar_url = (String) object.get("avatar");
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return out;
    }

    public void getWorkByPassword(Context context, String password, CallBack cb)
    {
        if (mHasStartLoadData)
        {
            return;
        }

        mHasStartLoadData = true;
        mCB = cb;
        if (mPasswordReqHandler != null)
        {
            Message msg = mPasswordReqHandler.obtainMessage();

            HashMap<String, Object> params = new HashMap<>();
            IPOCO ipoco = AppInterface.GetInstance(context);
            String mKey = ipoco.GetMKey();
            params.put("url", SysConfig.IsDebug() ? TEST_URL : URL);
            params.put("version", ipoco.GetAppVer());
            params.put("app_name", ipoco.GetAppName());
            params.put("imei", mKey);
            try
            {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("word", password);
                jsonObject.put("imei", mKey);

                params.put("param", jsonObject);

                msg.what = Msg.GET_WORK_START;
                msg.obj = params;
                mPasswordReqHandler.sendMessage(msg);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            mHasStartLoadData = true;
            mCB = null;
        }
    }

    public void clearAll()
    {
        mCB = null;
        if (mPasswordReqHandler != null)
        {
            mPasswordReqHandler.removeMessages(Msg.GET_WORK_START);
            mPasswordReqHandler = null;
        }

        if (mHandlerThread != null)
        {
            mHandlerThread.quit();
            mHandlerThread = null;
        }

        if (mMainHandler != null)
        {
            mMainHandler.removeMessages(Msg.GET_WORK_FAILED);
            mMainHandler.removeMessages(Msg.GET_WORK_SUCCEED);
            mMainHandler.removeMessages(Msg.GET_WORK_NULL);
            mMainHandler = null;
        }
    }

    private static class ErrorMsg
    {
        String mChinese = "";
        String mEnglish = "";
    }
}
