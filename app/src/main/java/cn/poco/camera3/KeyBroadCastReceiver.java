package cn.poco.camera3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.ArrayList;

/**
 * 监听锁屏键、home键
 * Created by Gxx on 2018/2/5.
 */

public class KeyBroadCastReceiver extends BroadcastReceiver
{
    private static ArrayList<OnKeyStatusListener> mListenerArr = new ArrayList<>();
    private static final Object mSynchronized = new Object();

    public interface OnKeyStatusListener
    {
        void onLongPressPowerKey();

        void onClickHomeKey();

        void onLongPressHomeKey();
    }

    private interface KeyStatusType
    {
        int IDLE = 0x1;
        int LONG_PRESS_POWER_KEY = 0x2;
        int CLICK_HOME_KEY = 0x3;
        int LONG_PRESS_HOME_KEY = 0x4;
    }

    public static void registerListener(OnKeyStatusListener listener)
    {
        if (listener != null && mListenerArr != null)
        {
            synchronized (mSynchronized)
            {
                mListenerArr.add(listener);
            }
        }
    }

    public static void unregisterListener(OnKeyStatusListener listener)
    {
        if (mListenerArr != null)
        {
            synchronized (mSynchronized)
            {
                mListenerArr.remove(listener);
            }
        }
    }

    private void notifyListener(int status_type)
    {
        if (mListenerArr != null)
        {
            synchronized (mSynchronized)
            {
                for (OnKeyStatusListener listener : mListenerArr)
                {
                    if (listener != null)
                    {
                        switch (status_type)
                        {
                            case KeyStatusType.LONG_PRESS_POWER_KEY:
                            {
                                listener.onLongPressPowerKey();
                                break;
                            }

                            case KeyStatusType.CLICK_HOME_KEY:
                            {
                                listener.onClickHomeKey();
                                break;
                            }

                            case KeyStatusType.LONG_PRESS_HOME_KEY:
                            {
                                listener.onLongPressHomeKey();
                                break;
                            }

                            default:
                                break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String reason = intent.getStringExtra("reason");
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
        {
            if (reason != null)
            {
                if (reason.equalsIgnoreCase("globalactions"))
                {
                    //监听电源长按键：
                    notifyListener(KeyStatusType.LONG_PRESS_POWER_KEY);
                }
                else if (reason.equalsIgnoreCase("homekey"))
                {
                    notifyListener(KeyStatusType.CLICK_HOME_KEY);
                }
                else if (reason.equalsIgnoreCase("recentapps"))
                {
                    //监听Home键长按
                    notifyListener(KeyStatusType.LONG_PRESS_HOME_KEY);
                }
            }
        }
    }
}
