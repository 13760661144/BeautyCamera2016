package cn.poco.video.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.Gravity;
import android.widget.FrameLayout;

import cn.poco.tianutils.FullScreenDlg;
import my.beautyCamera.R;

/**
 * @author lmx
 *         Created by lmx on 2017/9/6.
 */

public class AutoRoundProgressDialog extends FullScreenDlg
{
    protected AutoRoundProgressBar mBar;
    protected AutoRoundProgressView mView;

    public AutoRoundProgressDialog(Activity activity)
    {
        super(activity, R.style.autoRoundProgressDialog);
    }

    @Override
    protected void Init(Activity activity)
    {
        super.Init(activity);

        this.setCancelable(true);

        mView = new AutoRoundProgressView(activity);
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        fl.gravity = Gravity.CENTER;
        AddView(mView, fl);

        mBar = mView.getBar();
    }

    public void setBackgroundThumb(Bitmap bitmap)
    {
        if (bitmap != null && !bitmap.isRecycled() && mView != null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                mView.setBackground(new BitmapDrawable(getContext().getResources(), bitmap));
            }
            else
            {
                mView.setBackgroundDrawable(new BitmapDrawable(getContext().getResources(), bitmap));
            }
        }
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
        mView = null;
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
