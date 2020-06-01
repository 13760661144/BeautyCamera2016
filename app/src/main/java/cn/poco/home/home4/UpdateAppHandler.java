package cn.poco.home.home4;

import android.os.Handler;
import android.os.Message;

/**
 * Created by lgd on 2017/10/11.
 */

class UpdateAppHandler extends Handler
{
    public static final int MSG_UPDATE_APP = 1;
    public static final int MSG_UPDATE_TASK_HALL = 2;  //任务大厅红点
    protected Home4Page mPage;

    public UpdateAppHandler(Home4Page page)
    {
        mPage = page;
    }

    @Override
    public void handleMessage(Message msg)
    {
        switch (msg.what)
        {
            case MSG_UPDATE_APP:
                //不马上弹，先存储信息
//                if (mPage != null && msg.obj instanceof UpdateInfo)
//                {
//                    mPage.showUpdateAppVersionPopup((UpdateInfo) msg.obj);
//                }
                break;
            case MSG_UPDATE_TASK_HALL:
//                    Home4Page.sIsTaskHallHasRed = (boolean) msg.obj;
//                    if (mPage != null)
//                    {
//                        mPage.setTaskHallState(sIsTaskHallHasRed);
//                    }
                break;
            default:
                break;
        }
    }

    public void ClearAll()
    {
        mPage = null;
    }
}