package cn.poco.dynamicSticker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author lmx
 *         Created by lmx on 2017/7/18.
 */

public class HeadSetPlugReceiver extends BroadcastReceiver
{
    public static final String ACTION = Intent.ACTION_HEADSET_PLUG;

    private OnHeadSetPlugListener mListener;

    public HeadSetPlugReceiver(OnHeadSetPlugListener listener)
    {
        this.mListener = listener;
    }

    public void setListener(OnHeadSetPlugListener mListener)
    {
        this.mListener = mListener;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent != null && intent.getAction().equals(ACTION))
        {
            int state = intent.getIntExtra("state", 0);
            boolean in = false;
            if (state == 0)
            {
                in = false;
            }
            else if (state == 1)
            {
                in = true;
            }

            if (mListener != null)
            {
                mListener.onHeadSetPlugChange(in);
            }
        }
    }

    public interface OnHeadSetPlugListener
    {
        void onHeadSetPlugChange(boolean in);
    }
}
