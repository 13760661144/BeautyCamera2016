package cn.poco.camera3.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.StringRes;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cn.poco.camera3.config.MsgToastConfig;
import cn.poco.camera3.util.CameraPercentUtil;
import my.beautyCamera.R;

/**
 * @author lmx
 *         Created by lmx on 2017-12-28.
 */

public class PreviewBackMsgToast
{
    public MsgToastConfig mConfig;
    public String mMsg;

    public PreviewBackMsgToast()
    {
        MsgToastConfig config = new MsgToastConfig(MsgToastConfig.Key.PREVIEW_PAGE_SAVE_TOAST);
        config.setGravity(Gravity.CENTER_HORIZONTAL, 0, CameraPercentUtil.HeightPxToPercent(130));
        config.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        config.setTextColor(Color.WHITE);
        config.setTextBG(R.drawable.sticker_gif_title_bk);
        config.setTextBGAlpha(0.8f);
        mConfig = config;
    }

    public PreviewBackMsgToast setConfig(MsgToastConfig config)
    {
        this.mConfig = config;
        return this;
    }

    public PreviewBackMsgToast setMsg(Context context, @StringRes int msg)
    {
        return setMsg(context.getResources().getString(msg));
    }

    public PreviewBackMsgToast setMsg(String msg)
    {
        this.mMsg = msg;
        return this;
    }

    public void show(Context context)
    {
        Toast mToast = Toast.makeText(context, mMsg, Toast.LENGTH_SHORT);
        if (mConfig != null)
        {
            int xoffset = 0, yoffset = 0;
            switch (mConfig.getGravity() & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK)
            {
                case Gravity.END:
                {
                    xoffset = mConfig.getXOffset();
                    break;
                }

                default:
                {
                    xoffset = mConfig.getXOffset();
                }
            }
            switch (mConfig.getGravity() & Gravity.VERTICAL_GRAVITY_MASK)
            {
                case Gravity.BOTTOM:
                {
                    yoffset = mConfig.getYOffset();
                    break;
                }

                default:
                {
                    yoffset = mConfig.getYOffset();
                }
            }
            mToast.setGravity(mConfig.getGravity(), xoffset, yoffset);

            View view = mToast.getView();
            TextView textView = view.findViewById(android.R.id.message);
            if (textView != null)
            {
                if (mConfig.getTextBGDrawable() != null)
                {
                    setBGDrawable(view, mConfig.getTextBGDrawable());
                    setBGAlpha(view, mConfig.getTextBGAlpha());
                }
                else if (mConfig.getTextBGResID() != 0)
                {
                    setBG(view, mConfig.getTextBGResID());
                    setBGAlpha(view, mConfig.getTextBGAlpha());
                }
                else
                {
                    setBGDrawable(view, null);
                    setBGAlpha(view, mConfig.getTextBGAlpha());
                }
                textView.setTypeface(mConfig.getTextTypeFace());
                textView.setText(mMsg);
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(mConfig.getTextColor());
                textView.setShadowLayer(mConfig.getShadowRadius(), mConfig.getShadowDX(), mConfig.getShadowDY(), mConfig.getShadowColor());
                textView.setPadding(mConfig.getPaddingLeft(), mConfig.getPaddingTop(), mConfig.getPaddingRight(), mConfig.getPaddingBottom());
                textView.setTextSize(mConfig.getTextUnit(), mConfig.getTextSize());
            }
            else
            {
                mToast.setText(mMsg);
            }
        }
        else
        {
            mToast.setText(mMsg);
        }
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();

    }

    private void setBGAlpha(View view, float alpha)
    {
        if (view != null)
        {
            Drawable background = view.getBackground();
            if (background != null)
            {
                background.setAlpha((int) (255 * alpha));
            }
        }
    }

    private void setBGDrawable(View view, Drawable drawable)
    {
        if (view != null)
        {
            view.setBackgroundDrawable(drawable);
        }
    }

    private void setBG(View view, int resID)
    {
        if (view != null)
        {
            view.setBackgroundResource(resID);
        }
    }

    public void clear()
    {
        mConfig = null;
        mMsg = null;
    }
}
