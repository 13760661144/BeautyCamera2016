package cn.poco.video.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import cn.poco.camera3.util.CameraPercentUtil;
import my.beautyCamera.R;

/**
 * @author lmx
 *         Created by lmx on 2017/9/6.
 */

public class AutoRoundProgressView extends FrameLayout
{
    protected AutoRoundProgressBar mBar;

    public AutoRoundProgressView(@NonNull Context context)
    {
        super(context);
        init();
    }

    private void init()
    {
        FrameLayout frameLayout = new FrameLayout(getContext());
        FrameLayout.LayoutParams fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.CENTER;
        addView(frameLayout, fl);
        {
            mBar = new AutoRoundProgressBar(getContext());
            fl = new FrameLayout.LayoutParams(CameraPercentUtil.WidthPxToPercent(200), CameraPercentUtil.HeightPxToPercent(200));
            fl.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            frameLayout.addView(mBar, fl);

            TextView textView = new TextView(getContext());
            textView.setSingleLine(true);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            textView.setText(R.string.lightapp06_share_saving_video);
            fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            fl.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            fl.topMargin = CameraPercentUtil.HeightPxToPercent(50 + 200);
            frameLayout.addView(textView, fl);
        }


        TextView textView2 = new TextView(getContext());
        textView2.setGravity(Gravity.CENTER);
        textView2.setTextColor(Color.WHITE);
        textView2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        textView2.setLineSpacing(CameraPercentUtil.WidthPxToPercent(18), 1.0f);
        textView2.setText(R.string.lightapp06_share_saving_video_tips);
        fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        fl.bottomMargin = CameraPercentUtil.HeightPxToPercent(50);
        addView(textView2, fl);
    }

    public void setBackgroundThumb(Bitmap bitmap)
    {
        if (bitmap != null && !bitmap.isRecycled())
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                this.setBackground(new BitmapDrawable(getContext().getResources(), bitmap));
            }
            else
            {
                this.setBackgroundDrawable(new BitmapDrawable(getContext().getResources(), bitmap));
            }
        }
    }

    public AutoRoundProgressBar getBar()
    {
        return mBar;
    }

    public void start()
    {
        mBar.start();
    }

    public boolean isStart()
    {
        return mBar.isStart();
    }

    public void cancel()
    {
        mBar.cancel();
    }

    public void setFinishProgress(boolean finishProgress)
    {
        mBar.setFinishProgress(finishProgress);
    }

    public void setMaxProgress(int maxProgress)
    {
        mBar.setMaxProgress(maxProgress);
    }

    public void release()
    {
        mBar.release();
        mBar = null;
    }

    public void setListener(AutoRoundProgressBar.OnProgressListener listener)
    {
        mBar.setListener(listener);
    }

    public void stop()
    {
        mBar.stop();
    }
}
