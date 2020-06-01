package cn.poco.camera3.ui.shutter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import cn.poco.camera.CameraConfig;
import cn.poco.camera3.VideoMgr;
import cn.poco.camera3.cb.CameraPageListener;
import cn.poco.camera3.cb.ShutterAnimListener;
import cn.poco.camera3.config.CameraUIConfig;
import cn.poco.camera3.config.shutter.BaseConfig;
import cn.poco.camera3.config.shutter.DefConfig;
import cn.poco.camera3.config.shutter.RecordConfig;
import cn.poco.camera3.config.shutter.ShutterConfig;
import cn.poco.camera3.config.shutter.UnfoldResConfig;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import my.beautyCamera.R;

/**
 * Created by GAO-xx on 2017/5/13.
 * 由于不同的圆环区域，需要对点击坐标作判断，并决定是否做事件处理
 */

public class ShutterView extends View
{
    private int mCurrTabType;
    private int mCurrShutterType;
    private int mNextShutterType = -1;
    private float mCurrPreviewRatio;

    private DefConfig mDefConfig;
    private UnfoldResConfig mUnFoldResConfig;
    private RecordConfig mRecordConfig;

    private Paint mPaint;
    private Ring mRing; // 圆环
    private int mProgress; // 进度条弧度
    private boolean mIsDrawProgress;
    private Path mPath;
    private Region mRegion;
    private Region mTempRegion;

    // 暂停
    private boolean mIsDrawPauseLogo = false;
    private Bitmap mPauseBmp;
    private int mPauseBmpWH_720;
    private Matrix mMatrix;
    private float mPauseLogoAlpha = 0;
    private float mVideoInnerPauseAlpha = 1f; // 内圈圆透明度

    // 视频logo
    private Bitmap mVideoLogo;

    // 动画
    private ValueAnimator mValueAnim;
    private AnimatorListenerAdapter mAnimListener;
    private ShutterAnimListener mShutterAnimListener;

    private int mUIStatus = ShutterConfig.RecordStatus.CAN_RECORDED;
    private boolean mIsCancelAnim;
    private int mGIFTextRotation;
    private boolean mIsGifRotate;
    private int mVideoDuration;
    private boolean mIsDoingTransAnim;

    public ShutterView(Context context)
    {
        super(context);
        initData();
    }

    public boolean isCancelAnim()
    {
        return mIsCancelAnim;
    }

    private void initData()
    {
        // 初始化 快门样式
        mDefConfig = new DefConfig(getContext(), this);
        mUnFoldResConfig = new UnfoldResConfig(getContext(), this);
        mRecordConfig = new RecordConfig(getContext(), this);

        mPauseBmp = BitmapFactory.decodeResource(getResources(), R.drawable.camera_video_pause);
        mPauseBmpWH_720 = CameraPercentUtil.WidthPxToPercent(40);

        mVideoLogo = BitmapFactory.decodeResource(getResources(), R.drawable.video_shutter_logo);

        mMatrix = new Matrix();

        mRing = new Ring();
        mPaint = new Paint();
        mRegion = new Region();
        mTempRegion = new Region();
        mPath = new Path();

        // 记录video 分段录制进度
        mProgressArr = new ArrayList<>();
        mStartAngleArr = new ArrayList<>();
        mStartAngleArr.add(-90);
        mVideoDuration = TagMgr.GetTagIntValue(getContext(), Tags.CAMERA_RECORD_DURATION, 10);

        mValueAnim = ValueAnimator.ofFloat(0, 1);
        initAnimListener();
    }

    public void updateVideoDuration(int duration)
    {
        mVideoDuration = duration;
    }

    // ======================================== 设置参数 ==============================================//

    /**
     * 统一调用此方法，才开始改变快门样式
     */
    public void updateShutter()
    {
        mRing = AutoResetConfig(mCurrTabType, mCurrShutterType, mCurrPreviewRatio);
        UpDateUI();
    }

    public void setUIConfig(CameraUIConfig config)
    {
        if (config != null)
        {
            int type = mCurrTabType;
            int mode = mCurrShutterType;

            if (type == ShutterConfig.TabType.VIDEO && mode == ShutterConfig.ShutterType.PAUSE_RECORD)
            {
                resumePauseStatus(config);
            }
        }
    }

    public void setPreviewRatio(float ratio)
    {
        setAllParams(mCurrTabType, mCurrShutterType, ratio);
    }

    public void setMode(@ShutterConfig.ShutterType int mode)
    {
        setAllParams(mCurrTabType, mode, mCurrPreviewRatio);
    }

    public void setTabType(@ShutterConfig.TabType int type)
    {
        setAllParams(type, mCurrShutterType, mCurrPreviewRatio);
    }

    public void setAllParams(@ShutterConfig.TabType int tabType, @ShutterConfig.ShutterType int shutterType, float ratio)
    {
        if (ShutterConfig.checkTabTypeIsValid(tabType))
        {
            mCurrTabType = tabType;
        }
        if (ShutterConfig.checkShutterTypeIsValid(shutterType))
        {
            mCurrShutterType = shutterType;
        }
        if (checkPreviewRatioIsValid(ratio))
        {
            mCurrPreviewRatio = ratio;
        }
    }

    /**
     * 是否是有效的 预览比例
     *
     * @param ratio 需要检查的比例
     * @return 有效true
     */
    boolean checkPreviewRatioIsValid(float ratio)
    {
        return ratio == CameraConfig.PreviewRatio.Ratio_4_3
                || ratio == CameraConfig.PreviewRatio.Ratio_16_9
                || ratio == CameraConfig.PreviewRatio.Ratio_1_1
                || ratio == CameraConfig.PreviewRatio.Ratio_Full
                || ratio == CameraConfig.PreviewRatio.Ratio_9_16;
    }

    private Ring AutoResetConfig(int type, int mode, float ratio)
    {
        BaseConfig config = getConfigByMode(mode);

        return getRingByTypeAndRatio(type, ratio, config);
    }

    private Ring getRingByTypeAndRatio(int type, float ratio, BaseConfig config)
    {
        Ring out = null;
        switch (type)
        {
            case ShutterConfig.TabType.GIF: // gif 没有比例切换
            {
                out = config.GetGifRing();
                break;
            }

            case ShutterConfig.TabType.PHOTO: // 拍照 只有4:3、1:1
            {
                out = config.GetPhotoRing();
                break;
            }

            case ShutterConfig.TabType.CUTE:
            {
                if (ratio == CameraConfig.PreviewRatio.Ratio_16_9 || ratio == CameraConfig.PreviewRatio.Ratio_Full)
                {
                    out = config.Get169MakeupRing();
                }
                else
                {
                    out = config.Get43MakeupRing();
                }
                break;
            }

            case ShutterConfig.TabType.VIDEO: // 录像还有暂停
            {
                if (ratio == CameraConfig.PreviewRatio.Ratio_16_9 || ratio == CameraConfig.PreviewRatio.Ratio_Full)
                {
                    out = mPauseProgress ? config.Get169VideoPauseRing() : config.Get169VideoRing();
                }
                else
                {
                    out = mPauseProgress ? config.Get43VideoPauseRing() : config.Get43VideoRing();
                }
                break;
            }
        }

        return out;
    }

    private BaseConfig getConfigByMode(int mode)
    {
        BaseConfig out = null;
        switch (mode)
        {
            case ShutterConfig.ShutterType.DEF:
            {
                out = mDefConfig;
                break;
            }

            case ShutterConfig.ShutterType.UNFOLD_RES:
            {
                out = mUnFoldResConfig;
                break;
            }

            case ShutterConfig.ShutterType.PAUSE_RECORD:
            case ShutterConfig.ShutterType.RECORDING:
            {
                out = mRecordConfig;
                break;
            }
        }
        return out;
    }

    public void UpDateUI()
    {
        invalidate();
    }

    private void updateRing(Ring newRing, final ValueAnimator va)
    {
        if(mRing == null || newRing == null) return;

        if (mCurrTabType == ShutterConfig.TabType.CUTE && mCurrShutterType == ShutterConfig.ShutterType.DEF)
        {
            mRing.mRingColor = newRing.mRingColor;
            mRing.mInCircleColor = newRing.mInCircleColor;
        }

        if (mCurrTabType == ShutterConfig.TabType.GIF && mCurrShutterType == ShutterConfig.ShutterType.UNFOLD_RES)
        {
            mRing.mRingColor = newRing.mRingColor;
        }

        final float y = mRing.mCenter.y;
        final float rr = mRing.mRingRadius;
        final float rw = mRing.mRingWidth;
        final float inr = mRing.mInCircleRadius;
        final float ts = mRing.mTextSize;
        final float pr = mRing.mProgressRadius;
        final float inrx = mRing.mInCircleRx;
        final float inry = mRing.mInCircleRy;
        final float ina = mRing.mInnerAlpha;
        final float pw = mRing.mProgressWidth;
        final float scale = mRing.mAdaptionRadiusScale;
        final int offsetY = mRing.mAdaptionOffsetY;
        final int videoWH = mRing.mVideoLogoWH;

        final float dy = newRing.mCenter.y - y;
        final float der = newRing.mRingRadius - rr;
        final float drw = newRing.mRingWidth - rw;
        final float dir = newRing.mInCircleRadius - inr;
        final float dts = newRing.mTextSize - ts;
        final float dpr = newRing.mProgressRadius - pr;
        final float dinrx = newRing.mInCircleRx - inrx;
        final float dinry = newRing.mInCircleRy - inry;
        final float dina = newRing.mInnerAlpha - ina;
        final float dpw = newRing.mProgressWidth - pw;
        final float dscale = newRing.mAdaptionRadiusScale - scale;
        final int doffset = newRing.mAdaptionOffsetY - offsetY;
        final int dVideoWH = newRing.mVideoLogoWH - videoWH;

        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                float value = (float) animation.getAnimatedValue();
                PointF point = new PointF(mRing.mCenter.x, y + dy * value);
                float exR = rr + der * value;
                float ringW = rw + drw * value;
                float inR = inr + dir * value;
                float tS = ts + dts * value;
                float rpR = pr + dpr * value;
                float inRx = inrx + dinrx * value;
                float inRy = inry + dinry * value;
                float inA = ina + dina * value;
                float pW = pw + dpw * value;
                float Scale = scale + dscale * value;
                int Offset = (int) (offsetY + doffset * value * 1f);
                int vWH = (int) (videoWH + dVideoWH * value * 1f);
                updateParams(point, exR, inR, tS, ringW, rpR, inRx, inRy, inA, pW, Scale, Offset, vWH);
            }
        });
    }

    private void updateParams(PointF center, float exRadius, float inRadius, float tSize,
                              float ringW, float ringProgressR, float innerCircleRx,
                              float innerCircleRy, float innerAlpha, float progressW, float scale, int offsetY, int videoLogoWH)
    {
        mRing.mCenter = center;
        mRing.mRingRadius = exRadius;
        mRing.setRingRectF();
        mRing.mRingWidth = ringW;
        mRing.mInCircleRadius = inRadius;
        mRing.mInnerAlpha = innerAlpha;
        mRing.setInnerRoundRect(innerCircleRx, innerCircleRy);
        mRing.mTextSize = tSize;
        mRing.mProgressRadius = ringProgressR;
        mRing.mProgressWidth = progressW;
        mRing.setProgressRect();
        mRing.mAdaptionOffsetY = offsetY;
        mRing.mAdaptionRadiusScale = scale;
        mRing.mVideoLogoWH = videoLogoWH;
        UpDateUI();
    }

    public Animator InitTransformAnim(int nextMode, long duration)
    {
        mIsDoingTransAnim = true;
        Ring newRing = AutoResetConfig(mCurrTabType, nextMode, mCurrPreviewRatio);
        mNextShutterType = nextMode;
        updateRing(newRing, mValueAnim);
        mValueAnim.setDuration(duration);
        mValueAnim.addListener(mAnimListener);
        return mValueAnim;
    }

    public boolean isDoingTransAnim()
    {
        return mIsDoingTransAnim;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        mRegion.set(0, 0, w, h);

        mDefConfig.SetViewWidthAndHeight(w, h);
        mUnFoldResConfig.SetViewWidthAndHeight(w, h);
        mRecordConfig.SetViewWidthAndHeight(w, h);
        mRing = AutoResetConfig(mCurrTabType, mCurrShutterType, mCurrPreviewRatio);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        DrawShadowLayer(canvas, mRing);
        DrawCircle(canvas, mRing);
        DrawText(canvas, mRing);
        DrawVideoProgressUI(canvas, mRing);
        DrawGifProgress(canvas, mRing);
    }

    protected boolean mPauseProgress;
    protected int mProgressSize;
    protected int mProgressSum;

    private ArrayList<Integer> mProgressArr;
    private ArrayList<Integer> mStartAngleArr;

    public boolean mIsSelDelProgress = false;

    private boolean isCanSetProgress()
    {
        return mProgressSum >= 0 && mProgressSum < 360;
    }

    public void setIsDrawPauseLogo(boolean draw)
    {
        mIsDrawPauseLogo = draw;
    }

    /**
     * 检查已成功录制的视频个数
     *
     * @param realSize 真实视频个数
     */
    public void checkVideoSize(int realSize)
    {
        if (mProgressArr != null)
        {
            int size = mProgressArr.size();
            if (realSize < size)
            {
                mProgressSum -= mProgressArr.remove(size - 1);
                mProgressSize = mProgressArr.size();
                mProgress = 0;
                if (mStartAngleArr != null)
                {
                    mStartAngleArr.remove(size);
                }
            }
        }
    }

    private void resumePauseStatus(CameraUIConfig config)
    {
        mIsDrawProgress = true;
        mPauseProgress = true;

        if (mProgressArr == null)
        {
            mProgressArr = new ArrayList<>();
        }

        if (mStartAngleArr == null)
        {
            mStartAngleArr = new ArrayList<>();
            mStartAngleArr.add(-90);
        }

        Object obj = config.GetResetVideoPauseStatusInfo();
        if (obj != null && obj instanceof VideoMgr)
        {
            VideoMgr info = (VideoMgr) obj;
            mProgressSum = info.getVideoProgressSum();
            mProgressArr = info.getVideoProgressSumArr();
            mStartAngleArr = info.getVideoProgressAllAngleArr();
            mProgressSize = mProgressArr != null ? mProgressArr.size() : 0;
            mUIStatus = mProgressSum >= 360 ? ShutterConfig.RecordStatus.FULL_PROGRESS : ShutterConfig.RecordStatus.CAN_RECORDED;
        }
    }

    public void setUIEnable(int status)
    {
        mUIStatus = status;
        if (status == ShutterConfig.RecordStatus.CAN_RECORDED)
        {
            mVideoInnerPauseAlpha = 1f;
        }
    }

    public int getUIEnable() {
        return mUIStatus;
    }

    /**
     * 设置进度条
     *
     * @param progress 0 ~ 360°
     */
    public void setProgress(int progress)
    {
        if (mCurrTabType == ShutterConfig.TabType.GIF)
        {
            if (!mIsDrawProgress)
            {
                mIsDrawProgress = true;
            }
            mProgress = progress;
        }
        else if (mCurrTabType == ShutterConfig.TabType.VIDEO)
        {
            if (isCanSetProgress() && !mPauseProgress)
            {
                mIsDrawProgress = true;
                mProgress = progress;
                if (mProgressSum < 360)
                {
                    int sum = mProgressSum;
                    sum += progress;

                    if (sum >= 360)
                    {
                        sum = 360;
                        mProgress = sum - mProgressSum;
                        pauseProgress();
                        if(mCameraPageListener != null)
                        {
                            mCameraPageListener.onVideoProgressFull();
                        }
                    }
                }
            }
        }
        UpDateUI();
    }

    public void pauseProgress()
    {
        mPauseProgress = true;
        if (isCanSetProgress())
        {
            if(mVideoDuration == 10)
            {
                if(mProgress < 3)
                {
                    mProgress = 3;
                }
            }
            else
            {
                if(mProgress < 2)
                {
                    mProgress = 2;
                }
            }

            mPauseProgress = true;
            mProgressSum += mProgress;

            if (mProgressSum >= 360 && mProgressSize == 0)
            {
                mProgressArr.add(mProgress);
            }
            else
            {
                mProgressArr.add(mProgress - (mVideoDuration == 10 ? 2 : 1));
            }
            int lastAngle = mStartAngleArr.get(mProgressSize);
            mStartAngleArr.add(lastAngle + mProgress);
            mProgressSize = mProgressArr.size();
            mProgress = 0;

        }
        UpDateUI();
    }

    public void resumeProgress()
    {
        if (isCanSetProgress())
        {
            mPauseProgress = false;
        }
    }

    public void ResetProgress()
    {
        mProgress = 0;
        mProgressSum = 0;
        mProgressSize = 0;
        mProgressArr.clear();
        mStartAngleArr.clear();
        mStartAngleArr.add(-90);
        mIsDrawProgress = false;
        mUIStatus = ShutterConfig.RecordStatus.CAN_RECORDED;
        resumeProgress();
    }

    private int getProgressSize()
    {
        return mProgressArr.size();
    }

    public int deleteVideo()
    {
        if (!mPauseProgress) return -1;

        mIsSelDelProgress = false;
        int size = getProgressSize();
        if (size > 1)
        {
            mProgressSum = mProgressSum - mProgressArr.get(size - 1) - (mVideoDuration == 10 ? 2 : 1);
            mProgressArr.remove(size - 1);
            mStartAngleArr.remove(size);
        }
        else
        {
            ResetProgress();
        }
        mProgressSize = mProgressArr.size();
        if (mProgressSum < 360)
        {
            mUIStatus = ShutterConfig.RecordStatus.CAN_RECORDED;
        }
        UpDateUI();
        return mProgressSize;
    }

    public void selectLastVideo()
    {
        if (!mPauseProgress) return;
        mIsSelDelProgress = true;
        UpDateUI();
    }

    public boolean isAlreadySelLastVideo()
    {
        return mIsSelDelProgress;
    }

    public void resetSelectedStatus()
    {
        mIsSelDelProgress = false;
        UpDateUI();
    }

    public int getVideoSize()
    {
        return getProgressSize();
    }

    public void clearCurrentProgress()
    {
        mProgress = 0;
        mIsDrawProgress = false;
        mUIStatus = ShutterConfig.RecordStatus.CAN_RECORDED;

        switch (mCurrTabType)
        {
            case ShutterConfig.TabType.VIDEO:
            {
                if (getProgressSize() > 0)
                {
                    mPauseProgress = true;
                    mIsDrawProgress = true;
                }
                break;
            }
        }
    }

    private void DrawVideoProgressUI(Canvas canvas, Ring ring)
    {
        if (ring != null && mIsDrawProgress && mCurrTabType == ShutterConfig.TabType.VIDEO)
        {
            for (int i = 0; i < mProgressSize; i++)
            {
                if (mVideoDuration == CameraConfig.VideoDuration.TEN_SECOND)
                {
                    DrawVideoWhiteProgress(canvas, mStartAngleArr.get(i), mProgressArr.get(i), ring);
                }

                if (i == mProgressSize - 1 && mIsSelDelProgress)
                {
                    DrawVideoSelProgress(canvas, mStartAngleArr.get(i), mProgressArr.get(i), ring);
                }
                else
                {
                    DrawVideoProgress(canvas, mStartAngleArr.get(i), mProgressArr.get(i), ring);
                }
            }

            if (!mPauseProgress && mProgress != 0)
            {
                int startAngle = -90;
                if (mProgressSize > 0)
                {
                    startAngle = mStartAngleArr.get(mProgressSize);
                }
                DrawVideoProgress(canvas, startAngle, mProgress, ring);
            }
        }
    }

    private void DrawVideoWhiteProgress(Canvas canvas, int startAngle, int progress, Ring ring)
    {
        if (ring != null && mCurrTabType == ShutterConfig.TabType.VIDEO && mIsDrawProgress)
        {
            canvas.save();
            canvas.translate(ring.mCenter.x, ring.mCenter.y + ring.mAdaptionOffsetY);
            mPaint.reset();
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            mPaint.setFilterBitmap(true);
            mPaint.setStrokeWidth(ring.mProgressWidth);
            mPaint.setStyle(Paint.Style.STROKE);
            RectF rect = new RectF();
            rect.left = ring.mProgressRectF.left * ring.mAdaptionRadiusScale;
            rect.top = ring.mProgressRectF.top * ring.mAdaptionRadiusScale;
            rect.right = ring.mProgressRectF.right * ring.mAdaptionRadiusScale;
            rect.bottom = ring.mProgressRectF.bottom * ring.mAdaptionRadiusScale;
            mPaint.setColor(Color.WHITE);
            canvas.drawArc(rect, startAngle, progress + 2, false, mPaint);
            canvas.restore();
        }
    }

    private void DrawVideoProgress(Canvas canvas, int startAngle, int progress, Ring ring)
    {
        if (ring != null && mCurrTabType == ShutterConfig.TabType.VIDEO && mIsDrawProgress)
        {
            canvas.save();
            canvas.translate(ring.mCenter.x, ring.mCenter.y + ring.mAdaptionOffsetY);
            mPaint.reset();
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            mPaint.setFilterBitmap(true);
            mPaint.setStrokeWidth(ring.mProgressWidth);
            mPaint.setStyle(Paint.Style.STROKE);
            RectF rect = new RectF();
            rect.left = ring.mProgressRectF.left * ring.mAdaptionRadiusScale;
            rect.top = ring.mProgressRectF.top * ring.mAdaptionRadiusScale;
            rect.right = ring.mProgressRectF.right * ring.mAdaptionRadiusScale;
            rect.bottom = ring.mProgressRectF.bottom * ring.mAdaptionRadiusScale;
            mPaint.setColor(ring.mProgressColor);
            canvas.drawArc(rect, startAngle, progress, false, mPaint);
            canvas.restore();
        }
    }

    private void DrawVideoSelProgress(Canvas canvas, int startAngle, int progress, Ring ring)
    {
        if (ring != null && mCurrTabType == ShutterConfig.TabType.VIDEO && mIsDrawProgress)
        {
            canvas.save();
            canvas.translate(ring.mCenter.x, ring.mCenter.y + ring.mAdaptionOffsetY);
            mPaint.reset();
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            mPaint.setFilterBitmap(true);
            mPaint.setStrokeWidth(ring.mProgressWidth);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(ring.mProgressSelColor);
            RectF rect = new RectF();
            rect.left = ring.mProgressRectF.left * ring.mAdaptionRadiusScale;
            rect.top = ring.mProgressRectF.top * ring.mAdaptionRadiusScale;
            rect.right = ring.mProgressRectF.right * ring.mAdaptionRadiusScale;
            rect.bottom = ring.mProgressRectF.bottom * ring.mAdaptionRadiusScale;
            canvas.drawArc(rect, startAngle, progress, false, mPaint);
            canvas.restore();
        }
    }

    private void DrawGifProgress(Canvas canvas, Ring ring)
    {
        if (ring != null && mCurrTabType == ShutterConfig.TabType.GIF && mIsDrawProgress)
        {
            canvas.save();
            mPaint.reset();
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            mPaint.setFilterBitmap(true);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setColor(ring.mProgressColor);
            mPaint.setStrokeWidth(ring.mProgressWidth);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.translate(ring.mCenter.x, ring.mCenter.y + ring.mAdaptionOffsetY);
            RectF rect = new RectF();
            rect.left = ring.mProgressRectF.left * ring.mAdaptionRadiusScale;
            rect.top = ring.mProgressRectF.top * ring.mAdaptionRadiusScale;
            rect.right = ring.mProgressRectF.right * ring.mAdaptionRadiusScale;
            rect.bottom = ring.mProgressRectF.bottom * ring.mAdaptionRadiusScale;
            canvas.drawArc(rect, -90, mProgress, false, mPaint);
            canvas.restore();
        }
    }

    private void DrawShadowLayer(Canvas canvas, Ring ring)
    {
        if (ring != null && ring.mIsDrawShadow)
        {
            canvas.save();
            mPaint.reset();
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            mPaint.setFilterBitmap(true);
            RadialGradient s = new RadialGradient(0, 0, ring.mShadowRadius * ring.mAdaptionRadiusScale,
                    new int[]{ring.mShadowCenColor, ring.mShadowCenColor, ring.mShadowEdgeColor},
                    new float[]{0f, 0.5f, 1f}, Shader.TileMode.CLAMP);
            mPaint.setShader(s);
            canvas.translate(ring.mCenter.x, ring.mCenter.y + ring.mAdaptionOffsetY);
            canvas.drawCircle(0, 0, ring.mShadowRadius * ring.mAdaptionRadiusScale, mPaint);
            canvas.restore();
        }
    }

    public void setGIFRotation(int degree)
    {
        mGIFTextRotation = degree;
        switch (degree)
        {
            case 0:
            case 180:
            case 360:
            {
                mIsGifRotate = false;
                break;
            }

            case 90:
            case 270:
            case -90:
            {
                mIsGifRotate = true;
                break;
            }
        }
        UpDateUI();
    }

    private void DrawText(Canvas canvas, Ring ring)
    {
        if (ring != null && ring.mMidText != null)
        {
            canvas.save();
            canvas.rotate(mGIFTextRotation, ring.mCenter.x, ring.mCenter.y);
            mPaint.reset();
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            mPaint.setFilterBitmap(true);
            mPaint.setColor(ring.mTextColor);
            mPaint.setTextSize(ring.mTextSize);
            mPaint.setTypeface(Typeface.DEFAULT);
            mPaint.getTextBounds(ring.mMidText, 0, ring.mMidText.length(), ring.mTextRect);
            mPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(ring.mMidText, ring.mCenter.x + (mIsGifRotate ? ring.mAdaptionOffsetY : 0), ring.mCenter.y + ring.mTextRect.height() / 2f + (mIsGifRotate ? 0 : ring.mAdaptionOffsetY), mPaint);
            canvas.restore();
        }
    }

    private void DrawCircle(Canvas canvas, Ring ring)
    {
        if (ring != null && ring.mCenter != null)
        {
            canvas.save();
            mPaint.reset();
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            mPaint.setFilterBitmap(true);
            // 外圆
            mPaint.setColor(ring.mRingColor);
            canvas.translate(ring.mCenter.x, ring.mCenter.y + ring.mAdaptionOffsetY);
            if (ring.mRingIsDrawArc)
            {
                mPaint.setStrokeWidth(ring.mRingWidth);
                mPaint.setStyle(Paint.Style.STROKE);
                RectF rect = new RectF();
                rect.left = (int) (ring.mRingRectF.left * ring.mAdaptionRadiusScale);
                rect.top = (int) (ring.mRingRectF.top * ring.mAdaptionRadiusScale);
                rect.right = (int) (ring.mRingRectF.right * ring.mAdaptionRadiusScale);
                rect.bottom = (int) (ring.mRingRectF.bottom * ring.mAdaptionRadiusScale);
                canvas.drawArc(rect, -90f, 360f, false, mPaint);
            }
            else
            {
                canvas.drawCircle(0, 0, ring.mRingRadius * ring.mAdaptionRadiusScale, mPaint);
            }

            // 内圆
            mPaint.reset();
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            mPaint.setFilterBitmap(true);
            mPaint.setColor(ring.mInCircleColor);
            // 渐变
            switch (mUIStatus)
            {
                case ShutterConfig.RecordStatus.CAN_RECORDED:
                {
                    mPaint.setAlpha((int) (255 * ring.mInnerAlpha));
                    break;
                }

                case ShutterConfig.RecordStatus.FULL_PROGRESS: // 满进度 video
                {
                    mPaint.setAlpha((int) (255 * 0.1f));
                    break;
                }

                case ShutterConfig.RecordStatus.AT_THE_LAST_SEC: // 最后一秒 video
                {
                    if (mCurrTabType == ShutterConfig.TabType.VIDEO && mCurrShutterType == ShutterConfig.ShutterType.RECORDING)
                    {
                        if (ring.mInnerAlpha > 0.2f)
                        {
                            ring.mInnerAlpha -= 0.1f;
                        }
                        else
                        {
                            ring.mInnerAlpha = 0.1f;
                        }
                        mPaint.setAlpha((int) (255 * ring.mInnerAlpha));
                    }
                    break;
                }

                case ShutterConfig.RecordStatus.LESS_THAN_ONE_SEC: // 少于一秒 gif
                {
                    if (mCurrTabType == ShutterConfig.TabType.GIF && mCurrShutterType == ShutterConfig.ShutterType.RECORDING)
                    {
                        mPaint.setAlpha((int) (255 * ring.mInnerAlpha));
                        if (ring.mInnerAlpha > 0.96f)
                        {
                            ring.mInnerAlpha = 1f;
                        }
                        else
                        {
                            ring.mInnerAlpha += 0.04f;// 进度条 50ms 回调一次, 初始透明度 0.1f
                        }
                    }
                    break;
                }

                case ShutterConfig.RecordStatus.PAUSE_VIDEO:
                {
                    if (mCurrTabType == ShutterConfig.TabType.VIDEO)
                    {
                        if (mVideoInnerPauseAlpha > 0.2f)
                        {
                            mVideoInnerPauseAlpha -= 0.1f;
                        }
                        else
                        {
                            mVideoInnerPauseAlpha = 0.1f;
                        }
                        mPaint.setAlpha((int) (255 * mVideoInnerPauseAlpha));
                    }
                    break;
                }

                case ShutterConfig.RecordStatus.RESET_CAN_RECORDED:
                {
                    if (mCurrTabType == ShutterConfig.TabType.VIDEO)
                    {
                        if (mVideoInnerPauseAlpha < 0.9f)
                        {
                            mVideoInnerPauseAlpha += 0.07f;
                            invalidate();
                        }
                        else
                        {
                            setUIEnable(ShutterConfig.RecordStatus.CAN_RECORDED);
                        }
                        mPaint.setAlpha((int) (255 * mVideoInnerPauseAlpha));
                    }
                    break;
                }
            }
            RectF rect = new RectF();
            rect.left = (int) (ring.mInnerRoundRect.left * ring.mAdaptionRadiusScale);
            rect.top = (int) (ring.mInnerRoundRect.top * ring.mAdaptionRadiusScale);
            rect.right = (int) (ring.mInnerRoundRect.right * ring.mAdaptionRadiusScale);
            rect.bottom = (int) (ring.mInnerRoundRect.bottom * ring.mAdaptionRadiusScale);
            canvas.drawRoundRect(rect, ring.mInCircleRx, ring.mInCircleRy, mPaint);
            canvas.restore();

            // pause logo
            if (mIsDrawPauseLogo && mCurrTabType == ShutterConfig.TabType.VIDEO && mCurrShutterType == ShutterConfig.ShutterType.RECORDING)
            {
                canvas.save();
                mPaint.reset();
                mPaint.setAntiAlias(true);
                mPaint.setDither(true);
                mPaint.setFilterBitmap(true);

                // 渐变
                switch (mUIStatus)
                {
                    case ShutterConfig.RecordStatus.AT_THE_LAST_SEC:
                    case ShutterConfig.RecordStatus.PAUSE_VIDEO:
                    {
                        if (mPauseLogoAlpha > 0.11f)
                        {
                            mPauseLogoAlpha -= 0.05f;
                        }
                        else
                        {
                            mPauseLogoAlpha = 0;
                        }
                        break;
                    }

                    case ShutterConfig.RecordStatus.CAN_RECORDED:
                    {
                        if (mPauseLogoAlpha < 0.7f)
                        {
                            mPauseLogoAlpha += 0.2f;
                        }
                        else
                        {
                            mPauseLogoAlpha = 1;
                        }
                        break;
                    }
                }
                mPaint.setAlpha((int) (255 * mPauseLogoAlpha));

                mMatrix.reset();
                mMatrix.postScale(mPauseBmpWH_720 * 1f / mPauseBmp.getWidth(), mPauseBmpWH_720 * 1f / mPauseBmp.getHeight());
                mMatrix.postTranslate(ring.mCenter.x - mPauseBmpWH_720 / 2f, ring.mCenter.y - mPauseBmpWH_720 / 2f + ring.mAdaptionOffsetY);
                canvas.drawBitmap(mPauseBmp, mMatrix, mPaint);
                canvas.restore();
            }

            // video logo
            if (mCurrTabType == ShutterConfig.TabType.VIDEO && (mCurrShutterType == ShutterConfig.ShutterType.DEF || mCurrShutterType == ShutterConfig.ShutterType.UNFOLD_RES))
            {
                canvas.save();
                canvas.rotate(mGIFTextRotation, ring.mCenter.x, ring.mCenter.y);
                mPaint.reset();
                mPaint.setAntiAlias(true);
                mPaint.setDither(true);
                mPaint.setFilterBitmap(true);

                mMatrix.reset();
                float scale = ring.mVideoLogoWH * 1f / mVideoLogo.getWidth();
                mMatrix.postScale(scale, scale);
                mMatrix.postTranslate(ring.mCenter.x - ring.mVideoLogoWH / 2f + (mIsGifRotate ? ring.mAdaptionOffsetY : 0), ring.mCenter.y - ring.mVideoLogoWH / 2f + +(mIsGifRotate ? 0 : ring.mAdaptionOffsetY));
                canvas.drawBitmap(mVideoLogo, mMatrix, mPaint);
                canvas.restore();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float x = event.getX();
        float y = event.getY();

        if (mPath == null) return false;

        getCircleLocationInShutter();

        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_OUTSIDE || event.getAction() == MotionEvent.ACTION_CANCEL)
        {
            scaleShutter(mRing, 1f, 1f);
        }

        if (mTempRegion.contains((int) x, (int) y))
        {
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_UP)
            {
                boolean enable = false;

                if (mCameraPageListener != null && mCameraPageListener.isCountDown() && event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    scaleShutter(mRing, 0.94f, 0.94f);
                    return super.onTouchEvent(event);
                }

                switch (mCurrTabType)
                {
                    case ShutterConfig.TabType.GIF:
                    case ShutterConfig.TabType.VIDEO:
                    {
                        switch (mCurrShutterType)
                        {
                            case ShutterConfig.ShutterType.RECORDING:
                            {
                                if (mCameraPageListener != null && mCameraPageListener.isRecording() && mCameraPageListener.canPauseRecord())
                                {
                                    enable = true;
                                }
                                break;
                            }

                            default:
                            {
                                if ((mUIStatus == ShutterConfig.RecordStatus.CAN_RECORDED
                                        || mUIStatus == ShutterConfig.RecordStatus.RESET_CAN_RECORDED)
                                        && mCameraPageListener != null && mCameraPageListener.canRecord())
                                {
                                    enable = true;
                                }
                                break;
                            }
                        }
                        break;
                    }

                    case ShutterConfig.TabType.CUTE:
                    case ShutterConfig.TabType.PHOTO:
                    {
                        enable = true;
                        break;
                    }
                }

                if (enable && (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE))
                {
                    scaleShutter(mRing, 0.94f, 0.94f);
                }
                return !enable || mIsDoingTransAnim || super.onTouchEvent(event);
            }
        }
        else
        {
            scaleShutter(mRing, 1f, 1f);
            return false;
        }
        return false;
    }

    public Region getCircleLocationInShutter()
    {
        mPath.reset();
        mPath.addCircle(mRing.mCenter.x, mRing.mCenter.y, mRing.mShutterDiameter / 2f, Path.Direction.CW);
        mTempRegion.setEmpty();
        mTempRegion.setPath(mPath, mRegion);
        return mTempRegion;
    }

    private void scaleShutter(Ring ring, float scaleX, float scaleY)
    {
        if (ring == null || ring.mCenter == null) return;

        // 根据不同圆，设置缩放中心
        setPivotX(ring.mCenter.x);
        setPivotY(ring.mCenter.y);
        setScaleX(scaleX);
        setScaleY(scaleY);
    }

    public int GetMode()
    {
        return mCurrShutterType;
    }

    // ========================================== 监听 ========================================== //

    private CameraPageListener mCameraPageListener;

    public void setCameraPageListener(CameraPageListener listener)
    {
        mCameraPageListener = listener;
    }

    public void SetShutterAnimListener(ShutterAnimListener controller)
    {
        mShutterAnimListener = controller;
    }

    private void initAnimListener()
    {
        mAnimListener = new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                mIsCancelAnim = false;
                if (mShutterAnimListener != null)
                {
                    mShutterAnimListener.onShutterAnimStart(mCurrShutterType);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                mIsDoingTransAnim = false;
                if (mIsCancelAnim) return;
                setMode(mNextShutterType);
                mIsDrawPauseLogo = false;
                updateShutter();
                if (mNextShutterType == ShutterConfig.ShutterType.PAUSE_RECORD)
                {
                    mPauseLogoAlpha = 0;//避免暂停过程过短,透明度没有渐变至0
                }
                animation.removeAllListeners();
                ((ValueAnimator) animation).removeAllUpdateListeners();
                if (mShutterAnimListener != null)
                {
                    mShutterAnimListener.onShutterAnimEnd(mCurrShutterType);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                mIsDoingTransAnim = false;
                mIsCancelAnim = true;
                animation.removeAllListeners();
                ((ValueAnimator) animation).removeAllUpdateListeners();
                if (mShutterAnimListener != null) mShutterAnimListener.onShutterAnimCancel();
            }
        };
    }

    public void ClearMemory()
    {
        mIsCancelAnim = true;
        mAnimListener = null;
        mShutterAnimListener = null;
        mCameraPageListener = null;

        mDefConfig.ClearAll();
        mUnFoldResConfig.ClearAll();
        mRecordConfig.ClearAll();

        mValueAnim.removeAllUpdateListeners();
        mValueAnim.removeAllListeners();
        mValueAnim = null;

        mRegion = null;
        mTempRegion = null;
        mPath = null;
        mRing = null;
        mPaint = null;

        if (mPauseBmp != null && !mPauseBmp.isRecycled())
        {
            mPauseBmp.recycle();
            mPauseBmp = null;
        }

        if (mVideoLogo != null && !mVideoLogo.isRecycled())
        {
            mVideoLogo.recycle();
            mVideoLogo = null;
        }
    }
}
