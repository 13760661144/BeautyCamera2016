package cn.poco.camera3;

import android.content.Context;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.util.Observable;

import cn.poco.camera.CameraConfig;
import cn.poco.camera3.cb.CameraPageListener;
import cn.poco.camera3.cb.UIObservable;
import cn.poco.camera3.cb.UIObserver;
import cn.poco.camera3.config.CameraBtnConfig;
import cn.poco.camera3.config.CameraUIConfig;
import cn.poco.camera3.config.shutter.ShutterConfig;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.camera3.util.LanguageUtil;
import cn.poco.tianutils.ShareData;
import cn.poco.widget.PressedButton;
import my.beautyCamera.R;

public class CameraTopControlV3 extends FrameLayout implements View.OnClickListener, UIObserver
{
    private boolean mIsChinese;
    private PressedButton mCameraPatchBtn;// 镜头修正;
    private PressedButton mSettingBtn;// 设置
    private PressedButton mRatioBtn; // 比例
    private PressedButton mCameraSwitchBtn; // 切换镜头;

    private boolean mIsShowPatchBtn = false; // 是否显示镜头校正
    private boolean mIsShowCameraSwitch = true; // 是否显示镜头转换
    private boolean mIsShowSettingBtn = true; // 是否显示设置
    private boolean mIsShowRatio = true;
    private boolean mUIConfigShowRatio = true;
    private boolean mIsFrontMode;

    private boolean[] mBtnStatusArr;
    private boolean mBtnClickable = true;

    private int mTabType;
    private float mLastRatio;
    private float mRatio;
    private int mShutterMode;

    private int mBtnVisibleStatus;

    private int mIconWH = CameraPercentUtil.WidthPxToPercent(100);
    private int mIconSize;

    // 不同 ui 部件之间 通讯
    private Handler mUIHandler;
    private UIObservable mUIObserverList;
    private CameraPageListener mCameraPageListener;
    private boolean mIsIntercept;

    public CameraTopControlV3(@NonNull Context context)
    {
        super(context);
        mIsChinese = LanguageUtil.checkSystemLanguageIsChinese(context);
        initHandler();
        initView();
    }

    private void initHandler()
    {
        mUIHandler = new Handler(Looper.getMainLooper())
        {
            @Override
            public void handleMessage(Message msg)
            {
                switch (msg.what)
                {
                    case UIObserver.MSG_UNLOCK_UI:
                    {
                        if (mUIObserverList != null)
                        {
                            mUIObserverList.notifyObservers(UIObserver.MSG_UNLOCK_UI);
                        }
                        break;
                    }
                }
            }
        };
    }

    private void initView()
    {
        // 镜头校正
        mCameraPatchBtn = new PressedButton(getContext());
        mCameraPatchBtn.setVisibility(GONE);
        mCameraPatchBtn.setOnClickListener(this);
        FrameLayout.LayoutParams params = new LayoutParams(mIconWH, mIconWH);
        addView(mCameraPatchBtn, params);

        // 设置
        mSettingBtn = new PressedButton(getContext());
        mSettingBtn.setOnClickListener(this);
        mSettingBtn.setVisibility(GONE);
        params = new LayoutParams(mIconWH, mIconWH);
        addView(mSettingBtn, params);

        // 比例
        mRatioBtn = new PressedButton(getContext());
        mRatioBtn.setOnClickListener(this);
        mRatioBtn.setVisibility(GONE);
        params = new LayoutParams(mIconWH, mIconWH);
        addView(mRatioBtn, params);

        // 镜头切换
        mCameraSwitchBtn = new PressedButton(getContext());
        mCameraSwitchBtn.setVisibility(GONE);
        mCameraSwitchBtn.setOnClickListener(this);
        params = new LayoutParams(mIconWH, mIconWH);
        addView(mCameraSwitchBtn, params);

        initBtnStatusArr();
    }

    private void initBtnStatusArr()
    {
        int size = getChildCount();
        if (mBtnStatusArr == null)
        {
            mBtnStatusArr = new boolean[size];
        }
    }

    public void SetBtnAlpha(float alpha)
    {
        mCameraPatchBtn.setAlpha(alpha);
        mSettingBtn.setAlpha(alpha);
        mRatioBtn.setAlpha(alpha);
        if (mCameraSwitchBtn != null)
        {
            mCameraSwitchBtn.setAlpha(alpha);
        }
    }

    /**
     * @param show 是否显示 镜头校正
     */
    public void SetCameraPatchState(boolean show)
    {
        mIsShowPatchBtn = show;
    }

    public boolean GetCameraPatchState()
    {
        return mIsShowPatchBtn;
    }

    public PointF GetRatioLoc()
    {
        int[] loc = new int[2];

        if (mRatioBtn != null)
        {
            mRatioBtn.getLocationOnScreen(loc);
            loc[0] += mRatioBtn.getMeasuredWidth() / 2f;
            loc[1] += mRatioBtn.getMeasuredHeight();
        }

        return new PointF(loc[0], loc[1]);
    }

    public void SetCameraNum(int num)
    {
        if (num < 2 && mCameraSwitchBtn != null)
        {
            removeView(mCameraSwitchBtn);
            mCameraSwitchBtn = null;
        }
    }

    public boolean isFrontMode()
    {
        return mIsFrontMode;
    }

    public void setTabTy(int type)
    {
        mTabType = type;
        mIsShowRatio = mUIConfigShowRatio && type != ShutterConfig.TabType.GIF;
    }

    public void setCurrRatio(float ratio)
    {
        mLastRatio = mRatio;
        mRatio = ratio;
    }

    public float getCurrRatio()
    {
        return mRatio;
    }

    public void setUIConfig(CameraUIConfig config)
    {
        mIsShowCameraSwitch = config.isShowSwitchBtn();
        mIsShowPatchBtn = config.isShowPatchBtn();
        mIsShowSettingBtn = config.isShowSettingBtn();
        mUIConfigShowRatio = config.isShowRatioBtn();
        mIsFrontMode = config.isCameraFrontMode();
        mShutterMode = config.GetShutterMode();
        setTabTy(config.GetSelectedType());
        setCurrRatio(config.GetPreviewRatio());
        updateUI();
    }

    /**
     * 如果tab type 、预览比例有改变<br/>
     * 需要先调用 {@link CameraTopControlV3#setTabTy(int)} and {@link CameraTopControlV3#setCurrRatio(float)}
     */
    public void updateUI()
    {
        int type = mTabType;

        resetBtnVisibleStatus();

        switch (type)
        {
            case ShutterConfig.TabType.PHOTO:
            {
                setBtnVisibleType(mIsShowPatchBtn ? CameraBtnConfig.BarType.CAMERA_ADJUST : 0);
                break;
            }
        }

        setBtnVisibleType(mIsShowRatio ? CameraBtnConfig.BarType.CAMERA_RATIO : 0);
        setBtnVisibleType(mIsShowSettingBtn ? CameraBtnConfig.BarType.CAMERA_SETTING : 0);
        setBtnVisibleType(mIsShowCameraSwitch ? CameraBtnConfig.BarType.CAMERA_DIRECTION : 0);

        updateBtnStatus();
    }

    private void updateBtnStatus()
    {
        updateBtnSize();

        int showIconSize = 0;
        boolean isShow;

        isShow = containType(CameraBtnConfig.BarType.CAMERA_ADJUST);
        if (mCameraPatchBtn != null)
        {
            mCameraPatchBtn.setVisibility(isShow ? VISIBLE : GONE);
            showIconSize += isShow ? 1 : 0;
            mBtnStatusArr[CameraBtnConfig.BarBtnIndex.CAMERA_ADJUST] = isShow;
        }

        isShow = containType(CameraBtnConfig.BarType.CAMERA_SETTING);
        if (mSettingBtn != null)
        {
            mSettingBtn.setVisibility(isShow ? VISIBLE : GONE);
            showIconSize += isShow ? 1 : 0;
            mBtnStatusArr[CameraBtnConfig.BarBtnIndex.CAMERA_SETTING] = isShow;
        }

        isShow = containType(CameraBtnConfig.BarType.CAMERA_RATIO);
        if (mRatioBtn != null)
        {
            mRatioBtn.setVisibility(isShow ? VISIBLE : GONE);
            showIconSize += isShow ? 1 : 0;
            mBtnStatusArr[CameraBtnConfig.BarBtnIndex.CAMERA_RATIO] = isShow;
        }

        isShow = containType(CameraBtnConfig.BarType.CAMERA_DIRECTION);
        if (mCameraSwitchBtn != null)
        {
            mCameraSwitchBtn.setVisibility(isShow ? VISIBLE : GONE);
            showIconSize += isShow ? 1 : 0;
            mBtnStatusArr[CameraBtnConfig.BarBtnIndex.CAMERA_DIRECTION] = isShow;
        }

        updateBtnLogo();
        updateIconMargin(showIconSize);

        checkIsPauseMode();
    }

    private void checkIsPauseMode()
    {
        if (mShutterMode == ShutterConfig.ShutterType.PAUSE_RECORD)
        {
            if (mCameraPatchBtn != null)
            {
                mCameraPatchBtn.setVisibility(GONE);
            }

            if (mSettingBtn != null)
            {
                mSettingBtn.setVisibility(GONE);
            }

            if (mRatioBtn != null)
            {
                mRatioBtn.setVisibility(GONE);
            }
        }
    }

    private void updateIconMargin(int showSize)
    {
        // 计算两头、中间 icon 的间距
        int leftMargin = 0;
        int midMargin = 0;

        switch (showSize)
        {
            case 1:
            {
                if (mIsShowCameraSwitch)
                {
                    leftMargin = CameraPercentUtil.WidthPxToPercent(582); // 居右
                }
                else
                {
                    leftMargin = CameraPercentUtil.WidthPxToPercent(310);// 居中
                }
                break;
            }
            case 2:
            {
                leftMargin = CameraPercentUtil.WidthPxToPercent(60);
                midMargin = CameraPercentUtil.WidthPxToPercent(400);
                break;
            }
            case 3:
            {
                leftMargin = CameraPercentUtil.WidthPxToPercent(39);
                midMargin = CameraPercentUtil.WidthPxToPercent(171);
                break;
            }

            case 4:
            {
                leftMargin = CameraPercentUtil.WidthPxToPercent(39);
                midMargin = CameraPercentUtil.WidthPxToPercent(81);
                break;
            }

            case 5:
            {
                leftMargin = CameraPercentUtil.WidthPxToPercent(39);
                midMargin = CameraPercentUtil.WidthPxToPercent(36);
            }
        }

        int iconCount = mIconSize;
        int iconWH = mIconWH;
        int recordSize = 0;
        for (int i = 0; i < iconCount; i++)
        {
            boolean showBtn = mBtnStatusArr[i];

            if (!showBtn) continue;

            View view = getChildAt(i);

            if (view == null) continue;

            view.setTranslationX(leftMargin + (iconWH + midMargin) * recordSize);
            recordSize += 1;
        }
    }

    private void updateBtnSize()
    {
        mIconSize = getChildCount();
    }

    private boolean containType(@CameraBtnConfig.BarType int type)
    {
        return (mBtnVisibleStatus & type) != 0;
    }

    private void resetBtnVisibleStatus()
    {
        mBtnVisibleStatus &= CameraBtnConfig.ALL_BTN_GONE;
    }

    private void setBtnVisibleType(@CameraBtnConfig.BarType int type)
    {
        mBtnVisibleStatus |= type;
    }

    private void setBtnLogo(PressedButton view, int resID, int color, float alpha)
    {
        if (view == null) return;
        view.setButtonImage(resID, resID, color, alpha);
    }

    public void setBtnRotation(int degree)
    {
        if (mRatioBtn != null)
        {
            mRatioBtn.setRotate(degree);
        }
    }

    private void updateBtnLogo()
    {
        boolean isScreenMoreThan17_9 = (ShareData.m_screenRealHeight * 1f / ShareData.m_screenRealWidth) > (17f / 9f);

        float ratio = mRatio;
        float lastRatio = mLastRatio;

        boolean is1_1 = ratio == CameraConfig.PreviewRatio.Ratio_1_1;
        int resID = R.drawable.camera_top_ratio_1_1;
        float alpha = 0.5f;

        if (mRatioBtn != null)
        {
            if (ratio == CameraConfig.PreviewRatio.Ratio_4_3)
            {
                resID = isScreenMoreThan17_9 ? R.drawable.camera_top_ratio_3_4_gray :R.drawable.camera_top_ratio_3_4;
            }
            else if (ratio == CameraConfig.PreviewRatio.Ratio_16_9) // 竖屏
            {
                resID = isScreenMoreThan17_9 ? R.drawable.camera_top_ratio_9_16_gray : R.drawable.camera_top_ratio_9_16;
            }
            else if (ratio == CameraConfig.PreviewRatio.Ratio_Full)
            {
                resID = R.drawable.camera_top_ratio_full;
            }
            else if (ratio == CameraConfig.PreviewRatio.Ratio_9_16) // 横屏
            {
                resID = R.drawable.camera_top_ratio_16_9;
            }

            setBtnLogo(mRatioBtn, resID, 0, alpha);
        }

        if (lastRatio == ratio || (lastRatio == CameraConfig.PreviewRatio.Ratio_4_3 && ratio == CameraConfig.PreviewRatio.Ratio_16_9))
        {
            return;
        }

        if (mCameraPatchBtn != null)
        {
            if (is1_1)
            {
                resID = R.drawable.camera_layout_top_btn_fix_gray;
            }
            else if (ratio == CameraConfig.PreviewRatio.Ratio_9_16)
            {
                resID = R.drawable.camera_layout_top_btn_fix_gray;
            }
            else if (ratio == CameraConfig.PreviewRatio.Ratio_16_9 || ratio == CameraConfig.PreviewRatio.Ratio_4_3)
            {
                resID = isScreenMoreThan17_9 ? R.drawable.camera_layout_top_btn_fix_gray : R.drawable.camera_layout_top_btn_fix;
            }
            else
            {
                resID = R.drawable.camera_layout_top_btn_fix;
            }
            setBtnLogo(mCameraPatchBtn, resID, 0, alpha);
        }

        if (mSettingBtn != null)
        {
            if (is1_1)
            {
                resID = R.drawable.camera_setting_gray;
            }
            else if (ratio == CameraConfig.PreviewRatio.Ratio_9_16)
            {
                resID = R.drawable.camera_setting_gray;
            }
            else if (ratio == CameraConfig.PreviewRatio.Ratio_16_9 || ratio == CameraConfig.PreviewRatio.Ratio_4_3)
            {
                resID = isScreenMoreThan17_9 ? R.drawable.camera_setting_gray : R.drawable.camera_setting;
            }
            else
            {
                resID = R.drawable.camera_setting;
            }
            setBtnLogo(mSettingBtn, resID, 0, alpha);
        }

        if (mCameraSwitchBtn != null)
        {
            if (is1_1)
            {
                resID = R.drawable.camera_switch_gray;
            }
            else if (ratio == CameraConfig.PreviewRatio.Ratio_9_16)
            {
                resID = R.drawable.camera_switch_gray;
            }
            else if (ratio == CameraConfig.PreviewRatio.Ratio_16_9 || ratio == CameraConfig.PreviewRatio.Ratio_4_3)
            {
                resID = isScreenMoreThan17_9 ? R.drawable.camera_switch_gray : R.drawable.camera_switch;
            }
            else
            {
                resID = R.drawable.camera_switch;
            }
            setBtnLogo(mCameraSwitchBtn, resID, 0, alpha);
        }
    }

    public void SetButtonClickable(boolean clickable)
    {
        mBtnClickable = clickable;
    }

    public void SetUIObserver(@NonNull UIObservable UIObserver)
    {
        mUIObserverList = UIObserver;
        mUIObserverList.addObserver(this);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        return mIsIntercept || super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return mIsIntercept || super.onTouchEvent(event);
    }

    @Override
    public void onClick(View v)
    {
        if (!mBtnClickable) return;//当点击了拍照后其它操作无效

        if (mCameraPageListener != null && mCameraPageListener.isCountDown())
        {
            mCameraPageListener.onCancelCountDown();
        }

        lockUI();

        if (mCameraPageListener != null)
        {
            if (v == mCameraSwitchBtn)
            {
                mIsFrontMode = !mIsFrontMode;
                mCameraPageListener.onClickCameraSwitch();
                unlockUI(1000);
                return;
            }
            else if (v == mSettingBtn)
            {
                mCameraPageListener.onClickSetting();
            }
            else if (v == mRatioBtn)
            {
                mCameraPageListener.onClickRatioBtn();
                return;
            }
            else if (v == mCameraPatchBtn)
            {
                // 镜头校正过程，一直锁ui
                mCameraPageListener.onClickCameraPatch();
                return;
            }
        }

        unlockUI(500);
    }

    private void lockUI()
    {
        if (mUIObserverList != null)
        {
            mUIObserverList.notifyObservers(UIObserver.MSG_LOCK_UI);
        }
    }

    private void unlockUI(long daley)
    {
        if (mUIHandler != null)
        {
            if (daley == 0)
            {
                mUIHandler.sendEmptyMessage(UIObserver.MSG_UNLOCK_UI);
            }
            else
            {
                mUIHandler.sendEmptyMessageDelayed(UIObserver.MSG_UNLOCK_UI, daley);
            }
        }
    }

    public void setCameraPageListener(CameraPageListener listener)
    {
        mCameraPageListener = listener;
    }

    public void clearAll()
    {
        if (mUIObserverList != null)
        {
            mUIObserverList.deleteObserver(this);
            mUIObserverList = null;
        }

        mCameraPageListener = null;

        if (mCameraPatchBtn != null)
        {
            mCameraPatchBtn.setOnClickListener(null);
        }
        if (mSettingBtn != null)
        {
            mSettingBtn.setOnClickListener(null);
        }
        if (mCameraSwitchBtn != null)
        {
            mCameraSwitchBtn.setOnClickListener(null);
        }
        if (mRatioBtn != null)
        {
            mRatioBtn.setOnClickListener(null);
        }
        this.setOnClickListener(null);
        this.removeAllViews();
    }

    @Override
    public void update(Observable o, Object arg)
    {
        if (arg instanceof Integer)
        {
            int msg = (int) arg;
            switch (msg)
            {
                case UIObserver.MSG_LOCK_UI:
                {
                    mIsIntercept = true;
                    break;
                }

                case UIObserver.MSG_UNLOCK_UI:
                {
                    mIsIntercept = false;
                    break;
                }

                case UIObserver.MSG_ONLY_SHOW_CAMERA_SWITCH:
                {
                    if (this.getVisibility() == GONE)
                    {
                        setVisibility(VISIBLE);
                    }
                    if (mCameraPatchBtn != null)
                    {
                        mCameraPatchBtn.setVisibility(GONE);
                    }

                    if (mSettingBtn != null)
                    {
                        mSettingBtn.setVisibility(GONE);
                    }

                    if (mRatioBtn != null)
                    {
                        mRatioBtn.setVisibility(GONE);
                    }
                    mShutterMode = ShutterConfig.ShutterType.PAUSE_RECORD;
                    break;
                }

                case UIObserver.MSG_GONE_TOP_ALL_UI:
                {
                    this.setVisibility(GONE);
                    mShutterMode = ShutterConfig.ShutterType.RECORDING;
                    break;
                }

                case UIObserver.MSG_SHOW_TOP_ALL_UI:
                {
                    if (this.getVisibility() == GONE)
                    {
                        setVisibility(VISIBLE);
                    }
                    if (mCameraPatchBtn != null && mTabType == ShutterConfig.TabType.PHOTO && mIsShowPatchBtn)
                    {
                        mCameraPatchBtn.setVisibility(VISIBLE);
                    }

                    if (mSettingBtn != null && mIsShowSettingBtn)
                    {
                        mSettingBtn.setVisibility(VISIBLE);
                    }

                    if (mRatioBtn != null && mTabType != ShutterConfig.TabType.GIF && mIsShowRatio)
                    {
                        mRatioBtn.setVisibility(VISIBLE);
                    }

                    if (mCameraSwitchBtn != null && mIsShowCameraSwitch)
                    {
                        mCameraSwitchBtn.setVisibility(VISIBLE);
                    }
                    mShutterMode = -1;
                    break;
                }
            }
        }
    }

}
