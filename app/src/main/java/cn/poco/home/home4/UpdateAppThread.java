package cn.poco.home.home4;

import android.content.Context;
import android.os.Build;
import android.os.Handler;

import cn.poco.credits.Credit;
import cn.poco.system.AppInterface;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.web.PocoWebUtil;
import cn.poco.web.info.UpdateInfo;
import my.beautyCamera.R;

/**
 * Created by lgd on 2017/10/11.
 */

class UpdateAppThread extends Thread
{
    protected Context mContext;
    protected Handler mHandler;

    public UpdateAppThread(Context context, Handler handler)
    {
        mContext = context;
        mHandler = handler;
    }

    @Override
    public void run()
    {
        String userId = null;
        UpdateInfo info = PocoWebUtil.getAppUpdateInfo(userId, Build.VERSION.RELEASE, AppInterface.GetInstance(mContext));
        Credit.syncCreditIncome(mContext, mContext.getResources().getInteger(R.integer.积分_每天使用) + "");
        synchronized (this)
        {
            if (mHandler != null)
            {
                if (info != null)
                {
                    //先保存
                    final String data = info.getJsonCache();
                    if (data != null && data.length() > 0)
                    {
                        mHandler.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                TagMgr.SetTagValue(mContext, Tags.APP_UPDATE_INFO, data);
                            }
                        });
                    }

//						//通知ui
//                        if (hasRed)
//                        {
//                            Message msg = mHandler.obtainMessage();
//                            msg.what = UpdateAppHandler.MSG_UPDATE_TASK_HALL;
//                            msg.obj = hasRed;
//                            mHandler.sendMessage(msg);
//                        }
                }
            }
        }
    }
}