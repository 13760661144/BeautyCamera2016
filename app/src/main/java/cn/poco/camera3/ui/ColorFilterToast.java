package cn.poco.camera3.ui;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Gxx on 2017/11/7.
 */

public class ColorFilterToast
{
    private Handler mMainHandler;
    private ColorFilterTextView mContentView;
    private long mDuration = 1500;

    @interface Msg
    {
        int MSG_CANCEL_TOAST = 1 << 10;
    }

    public ColorFilterToast()
    {
        initHandler();
    }

    private void initHandler()
    {
        mMainHandler = new Handler(new Handler.Callback()
        {
            @Override
            public boolean handleMessage(Message message)
            {
                switch (message.what)
                {
                    case Msg.MSG_CANCEL_TOAST:
                    {
                        cancel();
                        break;
                    }
                }
                return false;
            }
        });
    }

    /**
     * @param parent 外层布局
     */
    public void setParent(FrameLayout parent)
    {
        if (parent == null) return;

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mContentView = new ColorFilterTextView(parent.getContext());
        parent.addView(mContentView, params);
    }

    public void updateToastMsgHeight(int height) {
        if (mContentView != null) {
            mContentView.updateToastMsgHeight(height);
        }
    }

    public void show(String msg)
    {
        if (msg != null && mContentView != null)
        {
            mContentView.setVisibility(View.VISIBLE);
            mContentView.setText(msg);
            mContentView.startScaleAnim();
            if (mMainHandler != null)
            {
                mMainHandler.removeMessages(Msg.MSG_CANCEL_TOAST);
                mMainHandler.sendEmptyMessageDelayed(Msg.MSG_CANCEL_TOAST, mDuration);
            }
        }
    }

    public void updateRotateAnimInfo(boolean isLandScape, int degree, float value)
    {
        if (mContentView != null)
        {
            mContentView.updateRotateAnimInfo(isLandScape, degree, value);
        }
    }

    public void setDuration(long duration)
    {
        mDuration = duration;
    }

    public void cancel()
    {
        if (mContentView != null)
        {
            mContentView.setVisibility(View.GONE);
        }

        if (mMainHandler != null)
        {
            mMainHandler.removeMessages(Msg.MSG_CANCEL_TOAST);
        }
    }

    public void ClearAll()
    {
        cancel();
        if (mContentView != null)
        {
            mContentView.ClearAll();
        }
        mMainHandler = null;
    }
}
