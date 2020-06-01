package cn.poco.camera3.config.shutter;

import android.content.Context;
import android.view.View;

import cn.poco.camera.CameraConfig;
import cn.poco.camera3.ui.shutter.Ring;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.tianutils.ShareData;

/**
 * @author Gxx
 *         Created by Gxx on 2017/8/18.
 */

public abstract class BaseConfig
{
    Ring mGifRing; // 表情包
    Ring mPhotoRing; // 4:3 & 1:1 拍照
    Ring mMakeupRing169; // 16:9 萌妆
    Ring mVideoRing169; // 16:9 视频
    Ring mMakeupRing43; // 4:3 & 1:1 萌妆
    Ring mVideoRing43; // 4:3 & 1:1 视频

    Ring mVideoPauseRing43; // 4:3 视频暂停
    Ring mVideoPauseRing169; // 16:9 视频暂停

    protected Context mContext;
    protected int mViewW;
    protected int mViewH;
    protected View mView;

    public BaseConfig(Context context, View view)
    {
        mContext = context;
        mView = view;
        ShareData.InitData(context);
        InitData();
        initPauseRing43();
        initPauseRing169();
    }

    public void SetViewWidthAndHeight(int w, int h)
    {
        mViewW = w;
        mViewH = h;
        InitCenter();
    }

    protected void initPauseRing169()
    {
    }

    protected void initPauseRing43()
    {
    }

    private void InitCenter()// 计算圆心位置
    {
        initRingCenter(mGifRing);
        initRingCenter(mPhotoRing);
        initRingCenter(mMakeupRing169);
        initRingCenter(mVideoRing169);
        initRingCenter(mMakeupRing43);
        initRingCenter(mVideoRing43);
        initRingCenter(mVideoPauseRing43);
        initRingCenter(mVideoPauseRing169);
    }

    private void initRingCenter(Ring ring)
    {
        if (ring != null)
        {
            ring.mCenter.x = mViewW / 2 - ring.mOffsetX;
            ring.mCenter.y = mViewH - ring.mOffsetY - ring.mShutterDiameter / 2f;
        }
    }

    private void InitData()
    {
        initGifData();
        initPhotoData();

        init169MakeupData();
        init43MakeupData();

        init169VideoData();
        init43VideoData();
    }

    public void AutoAdaptionStaticUI(boolean needAdaption)
    {
    }

    public void AutoAdaptionDynamicUI(boolean needAdaption)
    {
    }

    public Ring GetGifRing()
    {
        return mGifRing.copy();
    }

    public Ring Get169VideoRing()
    {
        return mVideoRing169.copy();
    }

    public Ring Get43VideoRing()
    {
        return mVideoRing43.copy();
    }

    public Ring GetPhotoRing()
    {
        return mPhotoRing.copy();
    }

    public Ring Get169MakeupRing()
    {
        return mMakeupRing169.copy();
    }

    public Ring Get43MakeupRing()
    {
        return mMakeupRing43.copy();
    }

    public Ring Get169VideoPauseRing()
    {
        return mVideoPauseRing169.copy();
    }

    public Ring Get43VideoPauseRing()
    {
        return mVideoPauseRing43.copy();
    }

    public void ClearAll()
    {
        mContext = null;
        mGifRing = null;
        mPhotoRing = null;
        mVideoRing169 = null;
        mMakeupRing169 = null;
        mView = null;
    }

    protected abstract void initGifData();

    protected abstract void initPhotoData();

    protected abstract void init169MakeupData();

    protected abstract void init169VideoData();

    protected abstract void init43MakeupData();

    protected abstract void init43VideoData();

    protected void autoAdaptionStaticUI(Ring ring, boolean needAdaption)
    {
        int dx = ShareData.m_screenRealHeight - ShareData.getScreenH();

        if (dx > 0 && needAdaption)
        {
            // 4:3背景top 那条线在屏幕的位置
            int m_4_3_line_loc = (int) (ShareData.m_screenRealWidth * CameraConfig.PreviewRatio.Ratio_4_3);

            int[] loc = new int[2];
            mView.getLocationOnScreen(loc);

            float radius = ring.mShutterDiameter / 2f;
            float circleCenterY = ring.mCenter.y;

            // 4:3 下圆心位置
            int m_4_3_center_y_loc = m_4_3_line_loc + (ShareData.getScreenH() - m_4_3_line_loc - CameraPercentUtil.HeightPxToPercent(90)) / 2;
            // 快门圆心在屏幕上的坐标
            int m_current_center_y_loc = (int) (loc[1] + circleCenterY);
            // 距离 4：3 白色背景顶部边线最小距离
            int mMinDistanceTo4_3 = CameraPercentUtil.HeightPxToPercent(8);
            // 计算新半径
            int newRadius = m_4_3_center_y_loc - (m_4_3_line_loc + mMinDistanceTo4_3);

            int dy = m_4_3_center_y_loc - m_current_center_y_loc;

            ring.mAdaptionOffsetY = dy > 0 ? dy : 0;

            // 超过的话，以 圆心到 4：3背景线为半径
            float scale = newRadius * 1f / radius;
            ring.mAdaptionRadiusScale = scale >= 1 ? 1 : scale;
        }
        else
        {
            ring.mAdaptionRadiusScale = 1f;
            ring.mAdaptionOffsetY = 0;
        }
    }

    protected void autoAdaptionDynamicUI(Ring ring, boolean needAdaption)
    {
        int dx = ShareData.m_screenRealHeight - ShareData.getScreenH();
        if (dx > 0 && needAdaption)
        {
            // 4:3背景top 那条线在屏幕的位置
            int m_4_3_line_loc = (int) (ShareData.m_screenRealWidth * CameraConfig.PreviewRatio.Ratio_4_3);

            int[] loc = new int[2];
            mView.getLocationOnScreen(loc);

            float radius = ring.mShutterDiameter / 2f;
            float circleCenterY = ring.mCenter.y;

            // 4:3 下圆心位置
            int m_4_3_center_y_loc = m_4_3_line_loc + (ShareData.getScreenH() - m_4_3_line_loc - CameraPercentUtil.HeightPxToPercent(90)) / 2;
            // 快门圆心在屏幕上的坐标
            int m_current_center_y_loc = (int) (loc[1] + circleCenterY - dx);
            // 距离 4：3 白色背景顶部边线最小距离
            int mMinDistanceTo4_3 = CameraPercentUtil.HeightPxToPercent(8);
            // 计算新半径
            int newRadius = m_4_3_center_y_loc - (m_4_3_line_loc + mMinDistanceTo4_3);

            int dy = m_4_3_center_y_loc - m_current_center_y_loc;

            ring.mAdaptionOffsetY = dy > 0 ? dy : 0;

            // 超过的话，以 圆心到 4：3背景线为半径
            float scale = newRadius * 1f / radius;
            ring.mAdaptionRadiusScale = scale >= 1 ? 1 : scale;
        }
        else
        {
            ring.mAdaptionRadiusScale = 1f;
            ring.mAdaptionOffsetY = 0;
        }
    }
}
