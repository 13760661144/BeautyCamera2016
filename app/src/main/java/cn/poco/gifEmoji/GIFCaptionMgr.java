package cn.poco.gifEmoji;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import cn.poco.tianutils.NetCore2;
import cn.poco.utils.MyNetCore;

/**
 * gif 字幕
 * Created by admin on 2017/6/2.
 */

public class GIFCaptionMgr
{
    private final int SUCCEED = 0;
    private final int FAILURE = 1;

    private ArrayList<GIFTextInfo> mLocalData;
    private String mTestUrl;
    private String mUrl;

    private Runnable mDownloadRunnable = null;
    private Thread mThreadTask;
    private Handler mUIHandler;
    private AsyncCallback mAsyncCallback;
    private MyNetCore mNet;

    public GIFCaptionMgr()
    {
        InitData();
    }

    private void InitData()
    {
        mTestUrl = "http://beauty-material.adnonstop.com/API/beauty_camera/gif_font/android.php?ver=88.8.8";
        mUrl = "http://beauty-material.adnonstop.com/API/beauty_camera/gif_font/android.php?ver=1.0.0";
        mLocalData = new ArrayList<>();

        mUIHandler = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(Message msg)
            {
                if (msg != null)
                {
                    switch (msg.what)
                    {
                        case SUCCEED:
                        {
                            Object obj = msg.obj;
                            if (obj instanceof ArrayList)
                            {
                                ArrayList data = (ArrayList) obj;
                                if (mAsyncCallback != null)
                                {
                                    mAsyncCallback.onSucceed(data);
                                }
                            }
                            break;
                        }

                        case FAILURE:
                        {
                            if (mAsyncCallback != null)
                            {
                                mAsyncCallback.onFailed();
                            }
                        }
                    }
                }
            }
        };
    }

    public ArrayList<GIFTextInfo> getLocalData()
    {
        return mLocalData;
    }

    public void ClearMemory()
    {
        clearLocalData();
        clearTask();

        mUIHandler.removeMessages(SUCCEED);
        mUIHandler.removeMessages(FAILURE);
        mUIHandler = null;

        mAsyncCallback = null;
    }

    private void clearLocalData()
    {
        mLocalData.clear();
        mLocalData = null;
    }

    private void clearTask()
    {
        if (mNet != null)
        {
            mNet.ClearAll();
            mNet = null;
        }

        mDownloadRunnable = null;

        if (mThreadTask != null)
        {
            mThreadTask.interrupt();
            mThreadTask = null;
        }
    }

    /**
     * 内置字幕
     */
    private void InitLocalData()
    {
        if (mLocalData.size() <= 0)
        {
            GIFTextInfo info = new GIFTextInfo();
            info.mName = "感觉自己萌萌哒";
            info.isLocal = true;
            mLocalData.add(info);

            info = new GIFTextInfo();
            info.mName = "臣妾做不到啊~";
            info.isLocal = true;
            mLocalData.add(info);
        }
    }

    private void initDownloadRunnable(final Context context)
    {
        if (mDownloadRunnable != null) return;

        mDownloadRunnable = new Runnable()
        {
            @Override
            public void run()
            {
                ArrayList<String> out = new ArrayList<>();

                if (mNet != null)
                {
                    mNet.ClearAll();
                }
                mNet = new MyNetCore(context);
                NetCore2.NetMsg msg = mNet.HttpGet(mUrl);
                if (msg != null && msg.m_stateCode == HttpURLConnection.HTTP_OK)
                {
                    byte[] data = msg.m_data;
                    if (data.length > 0)
                    {
                        String texts = new String(data);
                        try
                        {
                            JSONArray jsonArray = new JSONArray(texts);
                            int count = jsonArray.length();
                            for (int i = 0; i < count; i++)
                            {
                                Object obj = jsonArray.get(i);
                                if (obj instanceof String)
                                {
                                    out.add((String) obj);
                                }
                            }

                            if (mUIHandler != null)
                            {
                                Message message = mUIHandler.obtainMessage();
                                message.what = SUCCEED;
                                message.obj = out;
                                mUIHandler.sendMessage(message);
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                else
                {
                    if (mUIHandler != null)
                    {
                        Message message = mUIHandler.obtainMessage();
                        message.what = FAILURE;
                        mUIHandler.sendMessage(message);
                    }
                }
            }
        };
    }

    /**
     * load 后台字幕
     */
    public void LoadCaptionResArr(Context context, AsyncCallback callback)
    {
        mAsyncCallback = callback;
        InitLocalData();

        mAsyncCallback.onStart();

        initDownloadRunnable(context);

        mThreadTask = new Thread(mDownloadRunnable);
        mThreadTask.start();
    }

    public static interface AsyncCallback
    {
        void onStart();

        void onSucceed(ArrayList data);

        void onFailed();
    }
}
