package cn.poco.camera3.ui;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import cn.poco.camera3.config.MsgToastConfig;

/**
 * 自定义 toast
 * Created by Gxx on 2017/11/3.
 */

public class MsgToast
{
    private MsgToastConfig mConfig;
    private SparseArray<MsgToastConfig> mConfigList;
    private TextView mContentView;

    private long mDuration = 1500;

    private Handler mMainHandler;

    @interface Msg
    {
        int MSG_CANCEL_TOAST = 1 << 10;
    }

    public MsgToast()
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

    private void setShadow(float radius, float dx, float dy, int color)
    {
        if (mContentView != null)
        {
            mContentView.setShadowLayer(radius, dx, dy, color);
        }
    }

    private void setTextPadding(int left, int top, int right, int bottom)
    {
        if (mContentView != null)
        {
            mContentView.setPadding(left, top, right, bottom);
        }
    }

    private void setTextBG(int resID)
    {
        if (mContentView != null)
        {
            mContentView.setBackgroundResource(resID);
        }
    }

    private void setTextBGAlpha(float alpha)
    {
        if (mContentView != null)
        {
            Drawable background = mContentView.getBackground();
            if (background != null)
            {
                background.setAlpha((int) (255 * alpha));
            }
        }
    }

    private void setTextBGDrawable(Drawable drawable)
    {
        if (mContentView != null)
        {
            mContentView.setBackgroundDrawable(drawable);
        }
    }

    private void setText(String text)
    {
        if (mContentView != null)
        {
            mContentView.setText(text);
        }
    }

    private void setTextSize(int unit, int size)
    {
        if (mContentView != null)
        {
            mContentView.setTextSize(unit, size);
        }
    }

    private void setTextColor(int color)
    {
        if (mContentView != null)
        {
            mContentView.setTextColor(color);
        }
    }

    private void setTextGravity(int gravity)
    {
        if (mContentView != null)
        {
            mContentView.setGravity(gravity);
        }
    }

    private void setTypeface(Typeface type)
    {
        if (mContentView != null)
        {
            mContentView.setTypeface(type);
        }
    }

    /**
     * @param parent 外层布局
     */
    public void setParent(FrameLayout parent)
    {
        if (parent == null) return;

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mContentView = new TextView(parent.getContext());
        mContentView.setVisibility(View.GONE);
        parent.addView(mContentView, params);
    }

    public void setRotation(int degree)
    {
        if (mContentView != null)
        {
            mContentView.setRotation(degree);
        }
    }

    public MsgToastConfig getConfig(int key)
    {
        if(mConfigList != null)
        {
            return mConfigList.get(key);
        }

        return null;
    }

    public void addConfig(MsgToastConfig config)
    {
        if (mConfigList == null)
        {
            mConfigList = new SparseArray<>();
        }

        if (config != null)
        {
            mConfigList.put(config.getKey(), config);
        }
    }

    public void setDuration(long duration)
    {
        mDuration = duration;
    }

    public void show(@MsgToastConfig.Key int toast_key, String msg)
    {
        if (mContentView == null) return;

        if (mMainHandler != null)
        {
            mMainHandler.removeMessages(Msg.MSG_CANCEL_TOAST);
        }

        if (mConfigList != null)
        {
            mConfig = mConfigList.get(toast_key);
        }

        if (mConfig != null && mContentView != null)
        {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = mConfig.getGravity();
            switch (mConfig.getGravity() & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK)
            {
                case Gravity.END:
                {
                    params.rightMargin = mConfig.getXOffset();
                    break;
                }

                default:
                {
                    params.leftMargin = mConfig.getXOffset();
                }
            }
            switch (mConfig.getGravity() & Gravity.VERTICAL_GRAVITY_MASK)
            {
                case Gravity.BOTTOM:
                {
                    params.bottomMargin = mConfig.getYOffset();
                    break;
                }

                default:
                {
                    params.topMargin = mConfig.getYOffset();
                }
            }

            if (mConfig.getTextBGDrawable() != null)
            {
                setTextBGDrawable(mConfig.getTextBGDrawable());
                setTextBGAlpha(mConfig.getTextBGAlpha());
            }
            else if (mConfig.getTextBGResID() != 0)
            {
                setTextBG(mConfig.getTextBGResID());
                setTextBGAlpha(mConfig.getTextBGAlpha());
            }
            else
            {
                setTextBGDrawable(null);
                setTextBGAlpha(mConfig.getTextBGAlpha());
            }
            setTypeface(mConfig.getTextTypeFace());

            if (msg != null)
            {
                setText(msg);
            }
            setTextGravity(Gravity.CENTER);
            setTextColor(mConfig.getTextColor());
            setShadow(mConfig.getShadowRadius(), mConfig.getShadowDX(), mConfig.getShadowDY(), mConfig.getShadowColor());
            setTextPadding(mConfig.getPaddingLeft(), mConfig.getPaddingTop(), mConfig.getPaddingRight(), mConfig.getPaddingBottom());
            setTextSize(mConfig.getTextUnit(), mConfig.getTextSize());
            mContentView.setLayoutParams(params);
        }

        mContentView.setVisibility(View.VISIBLE);

        if(mMainHandler != null)
        {
            mMainHandler.sendEmptyMessageDelayed(Msg.MSG_CANCEL_TOAST, mDuration);
        }
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
        if (mConfigList != null)
        {
            mConfigList.clear();
            mConfigList = null;
        }

        mConfig = null;
        mMainHandler = null;
    }
}
