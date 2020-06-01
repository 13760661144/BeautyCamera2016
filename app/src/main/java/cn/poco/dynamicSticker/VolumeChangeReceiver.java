package cn.poco.dynamicSticker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author lmx
 *         Created by lmx on 2017/7/24.
 */

public class VolumeChangeReceiver extends BroadcastReceiver
{
    public static final String ACTION = "android.media.VOLUME_CHANGED_ACTION";

    private OnVolumeChangedListener mListener;

    public VolumeChangeReceiver(OnVolumeChangedListener mListener)
    {
        this.mListener = mListener;
    }

    public void setListener(OnVolumeChangedListener mListener)
    {
        this.mListener = mListener;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent != null && intent.getAction()!=null && intent.getAction().equals(ACTION))
        {
            if (mListener != null)
            {
                mListener.onVolumeChanged();
            }
        }
    }


    public static interface OnVolumeChangedListener
    {
        public void onVolumeChanged();
    }
}
