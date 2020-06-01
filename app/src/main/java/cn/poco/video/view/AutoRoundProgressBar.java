package cn.poco.video.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.tianutils.ShareData;

/**
 * @author lmx
 *         Created by lmx on 2017/9/4.
 */

public class AutoRoundProgressBar extends View implements Handler.Callback
{
    protected Typeface mTypeFace;
    protected DrawFilter mDrawFilter;

    protected Paint mPaint;
    protected Paint mPaintText;

    protected int mMaxW;
    protected int mMaxH;
    protected int mRoundW;

    protected int mMaxProgress;
    protected int mProgress;
    protected int mStopProgress;        //若未结束，最大倒计时到该进度，小于mMaxProgress

    protected int mRoundColor;
    protected int mRoundProgressColor;
    protected int mTextColor;
    protected int mTextSize;

    protected long mMaxMillis;          //倒计时最长时间（单位：毫秒）
    protected long mIntervalMills;      //触发时间间隔（单位：毫秒）

    protected boolean isShowText = true;
    protected boolean isFinishProgress = false;
    protected boolean isStart = false;

    protected OnProgressListener mListener;
    protected Handler mHandler;

    public interface OnProgressListener
    {
        void onProgress(int progress);

        void onFinish();
    }

    public AutoRoundProgressBar(Context context)
    {
        super(context);
        init();
    }

    private void init()
    {
        mMaxW = CameraPercentUtil.WidthPxToPercent(200);
        mMaxH = mMaxW;

        mRoundW = CameraPercentUtil.WidthPxToPercent(2);
        mTextSize = CameraPercentUtil.WidthPxToPercent(100);

        mMaxProgress = 100;
        mStopProgress = (int) (mMaxProgress * 0.98f);

        //2分钟
        mMaxMillis = (60L * 4) * 1000L;
        mIntervalMills = mMaxMillis / mMaxProgress;

        mTextColor = 0xFFFFFFFF;
        mRoundColor = 0x21FFFFFF;
        mRoundProgressColor = 0xFFFFFFFF;

        try
        {
            mTypeFace = Typeface.createFromAsset(getContext().getAssets(), "fonts/code_light.otf");
        }
        catch (Throwable t)
        {
            mTypeFace = null;
        }
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        mPaint = new Paint();
        mPaintText = new Paint();
        mHandler = new Handler(Looper.getMainLooper(), this);
    }

    public void setListener(OnProgressListener mListener)
    {
        this.mListener = mListener;
    }

    public void start()
    {
        if (isStart) return;

        isStart = true;
        mHandler.sendEmptyMessageDelayed(0, 500);
    }

    public boolean isStart()
    {
        return isStart;
    }

    public synchronized void cancel()
    {
        stop();
        isFinishProgress = false;
        mProgress = 0;
    }

    public synchronized void stop()
    {
        isStart = false;
        if (mHandler != null) mHandler.removeMessages(0);
    }

    /**
     * 触发时间间隔（单位：毫秒）
     *
     * @param intervalMills
     */
    public void setIntervalMills(long intervalMills)
    {
        this.mIntervalMills = intervalMills;
    }

    /**
     * 若未结束，最大倒计时到该进度，小于mMaxProgress
     *
     * @param mStopProgress
     */
    public void setStopProgress(int mStopProgress)
    {
        if (mStopProgress >= mMaxProgress)
        {
            mStopProgress = (int) (mMaxProgress * 0.9f);
        }
        else if (mStopProgress >= this.mProgress)
        {
            mStopProgress = (int) (mMaxProgress * 0.9f);
        }

        this.mStopProgress = mStopProgress;
    }

    private synchronized void setProgress(int progress)
    {
        if (progress < 0)
        {
            progress = 0;
        }

        if (progress > mMaxProgress)
        {
            progress = mMaxProgress;
        }
        mProgress = progress;
        this.invalidate();
    }

    public synchronized void setFinishProgress(boolean isFinishProgress)
    {
        this.isFinishProgress = isFinishProgress;
        if (mHandler != null) mHandler.sendEmptyMessage(0);
    }

    public synchronized boolean isFinish()
    {
        return mProgress == mMaxProgress;
    }

    public synchronized void setMaxProgress(int max)
    {
        if (max < 0)
        {
            max = 0;
        }

        if (max != mMaxProgress)
        {
            mMaxProgress = max;
            postInvalidate();

            if (mProgress > max)
            {
                mProgress = max;
            }
            this.invalidate();
        }
    }

    public void release()
    {
        isStart = false;
        if (mHandler != null)
        {
            mHandler.removeMessages(0);
        }
        mHandler = null;
        mTypeFace = null;
        mDrawFilter = null;
        mPaint = null;
        mPaintText = null;
    }

    @Override
    public boolean handleMessage(Message msg)
    {
        if (!isStart) return true;

        int progress = mProgress;
        if (progress <= mMaxProgress)
        {
            setProgress(progress);

            if (mListener != null) {
                mListener.onProgress(progress);
            }
            if (progress == mMaxProgress) {
                isStart = false;
                if (mListener != null) {
                    mListener.onFinish();
                }
                return true;
            }

            boolean sendMsg = false;
            if (!isFinishProgress && progress < mStopProgress) {
                mProgress += 1;
                sendMsg = true;
            } else if (isFinishProgress) {
                mProgress += 1;
                sendMsg = true;
            }

            if (sendMsg && mProgress <= mMaxProgress && mHandler != null)
            {
                mHandler.sendEmptyMessageDelayed(0, isFinishProgress ? 100 : mIntervalMills);
            }
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.save();
        canvas.setDrawFilter(mDrawFilter);
        drawProgress(canvas);
        drawText(canvas);
        canvas.restore();
    }

    private void drawProgress(Canvas canvas)
    {
        int center = mMaxW / 2;
        int radius = center - mRoundW / 2;
        mPaint.reset();
        mPaint.setColor(mRoundColor);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mRoundW);
        canvas.drawCircle(center, center, radius, mPaint);

        mPaint.setStrokeWidth(mRoundW);
        mPaint.setColor(mRoundProgressColor);
        RectF rect = new RectF(center - radius, center - radius, center + radius, center + radius);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(rect, -90, 360f * mProgress / mMaxProgress, false, mPaint);
    }

    private void drawText(Canvas canvas)
    {
        int center = mMaxW / 2;
        mPaint.reset();
        mPaint.setStrokeWidth(0);
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
        mPaint.setTypeface(mTypeFace); //设置字体
        //中间的进度百分比，先转换成float在进行除法运算，不然都为0
        int percent = (int) (((float) mProgress / (float) mMaxProgress) * 100);
        //测量字体宽度，我们需要根据字体的宽度设置在圆环中间
        float textWidth = mPaint.measureText(String.valueOf(percent));

        mPaintText.reset();
        mPaintText.setStrokeWidth(0);
        mPaintText.setColor(mTextColor);
        mPaintText.setTextSize(ShareData.PxToDpi_xhdpi(14));
        if (mTypeFace != null)
        {
            mPaintText.setTypeface(mTypeFace);
        }

        float percentWidth = mPaintText.measureText("%");
        float width = textWidth + percentWidth;
        String text = String.valueOf(percent);
        if (isShowText)
        {
            canvas.drawText(text, center - width / 2, center + mTextSize / 3, mPaint);
            canvas.drawText("%", center - width / 2 + textWidth + 5, center + mTextSize / 3, mPaintText);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int min = Math.min(width, height);
        int min1 = Math.min(mMaxW, mMaxH);
        if (min > min1)
        {
            min = min1;
            mMaxW = width;
            mMaxH = height;
        }
        setMeasuredDimension(min, min);
    }
}
