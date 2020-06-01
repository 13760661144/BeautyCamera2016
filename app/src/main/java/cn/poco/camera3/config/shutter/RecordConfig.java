package cn.poco.camera3.config.shutter;

import android.content.Context;
import android.view.View;

import cn.poco.camera.CameraConfig;
import cn.poco.camera3.ui.shutter.Ring;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.tianutils.ShareData;

/**
 * @author Gxx
 *         Created by Gxx on 2017/8/15.
 *         四种类型快门的 录制过程 配置
 */

public class RecordConfig extends BaseConfig
{

    public RecordConfig(Context context, View view)
    {
        super(context, view);
    }

    @Override
    protected void initGifData() // 1:1 gif 录制参数
    {
        mGifRing = new Ring();
        mGifRing.mShutterDiameter = CameraPercentUtil.WidthPxToPercent(210);
        mGifRing.mOffsetY = CameraPercentUtil.WidthPxToPercent(84);

        mGifRing.mInCircleColor = 0xffffcc00;
        mGifRing.mInnerAlpha = 0.1f;
        mGifRing.mInCircleRadius = CameraPercentUtil.WidthPxToPercent(35);
        mGifRing.setInnerRoundRect(CameraPercentUtil.WidthPxToPercent(14), CameraPercentUtil.WidthPxToPercent(14));

        mGifRing.mRingIsDrawArc = false;
        mGifRing.mRingColor = 0xfff5f5f5;
        mGifRing.mRingRadius = CameraPercentUtil.WidthPxToPercent(105);

        mGifRing.mProgressColor = 0xffffcc00;
        mGifRing.mProgressWidth = CameraPercentUtil.WidthPxToPercent(10);
        mGifRing.mProgressRadius = CameraPercentUtil.WidthPxToPercent(100);
        mGifRing.setProgressRect();
    }

    @Override
    protected void init169VideoData() // 16:9 视频 录制参数
    {
        mVideoRing169 = new Ring();
        mVideoRing169.mShutterDiameter = CameraPercentUtil.WidthPxToPercent(210);
        mVideoRing169.mOffsetY = CameraPercentUtil.WidthPxToPercent(84);

        mVideoRing169.mInCircleColor = 0xfffc3745;
        mVideoRing169.mInCircleRadius = CameraPercentUtil.WidthPxToPercent(47);
        mVideoRing169.setInnerRoundRect(-1, -1);

        mVideoRing169.mRingIsDrawArc = false;
        mVideoRing169.mRingColor = 0x99ffffff;
        mVideoRing169.mRingRadius = CameraPercentUtil.WidthPxToPercent(105);

        mVideoRing169.mProgressColor = 0xfffc3745;
        mVideoRing169.mProgressWidth = CameraPercentUtil.WidthPxToPercent(10);
        mVideoRing169.mProgressRadius = CameraPercentUtil.WidthPxToPercent(100);
        mVideoRing169.setProgressRect();
    }

    @Override
    protected void init43VideoData() // 4:3 视频 录制参数
    {
        mVideoRing43 = new Ring();
        mVideoRing43.mShutterDiameter = CameraPercentUtil.WidthPxToPercent(210);
        mVideoRing43.mOffsetY = CameraPercentUtil.WidthPxToPercent(84);

        mVideoRing43.mInCircleColor = 0xfffc3745;
        mVideoRing43.mInCircleRadius = CameraPercentUtil.WidthPxToPercent(47);
        mVideoRing43.setInnerRoundRect(-1, -1);

        mVideoRing43.mRingIsDrawArc = false;
        mVideoRing43.mRingColor = 0xfff0f0f0;
        mVideoRing43.mRingRadius = CameraPercentUtil.WidthPxToPercent(105);

        mVideoRing43.mProgressColor = 0xfffc3745;
        mVideoRing43.mProgressWidth = CameraPercentUtil.WidthPxToPercent(10);
        mVideoRing43.mProgressRadius = CameraPercentUtil.WidthPxToPercent(100);
        mVideoRing43.setProgressRect();
    }

    @Override
    protected void initPauseRing169() // 16:9 视频 录制暂停参数
    {
        mVideoPauseRing169 = new Ring();
        mVideoPauseRing169.mShutterDiameter = CameraPercentUtil.WidthPxToPercent(146);
        mVideoPauseRing169.mOffsetY = CameraPercentUtil.WidthPxToPercent(115);

        mVideoPauseRing169.mInCircleColor = 0xfffc3745;
        mVideoPauseRing169.mInCircleRadius = CameraPercentUtil.WidthPxToPercent(59);
        mVideoPauseRing169.setInnerRoundRect(-1, -1);

        mVideoPauseRing169.mRingIsDrawArc = false;
        mVideoPauseRing169.mRingColor = 0x99ffffff;
        mVideoPauseRing169.mRingRadius = CameraPercentUtil.WidthPxToPercent(72);

        mVideoPauseRing169.mProgressColor = 0xfffc3745;
        mVideoPauseRing169.mProgressSelColor = 0x99fc3745;
        mVideoPauseRing169.mProgressRadius = CameraPercentUtil.WidthPxToPercent(70);
        mVideoPauseRing169.mProgressWidth = CameraPercentUtil.WidthPxToPercent(5);
        mVideoPauseRing169.setProgressRect();
    }

    @Override
    protected void initPauseRing43() // 4:3 视频 录制暂停参数
    {
        mVideoPauseRing43 = new Ring();
        mVideoPauseRing43.mShutterDiameter = CameraPercentUtil.WidthPxToPercent(146);
        mVideoPauseRing43.mOffsetY = CameraPercentUtil.WidthPxToPercent(115);

        mVideoPauseRing43.mInCircleColor = 0xfffc3745;
        mVideoPauseRing43.mInCircleRadius = CameraPercentUtil.WidthPxToPercent(59);
        mVideoPauseRing43.setInnerRoundRect(-1, -1);

        mVideoPauseRing43.mRingIsDrawArc = false;
        mVideoPauseRing43.mRingColor = 0xfff0f0f0;
        mVideoPauseRing43.mRingRadius = CameraPercentUtil.WidthPxToPercent(72);

        mVideoPauseRing43.mProgressColor = 0xfffc3745;
        mVideoPauseRing43.mProgressSelColor = 0x99fc3745;
        mVideoPauseRing43.mProgressRadius = CameraPercentUtil.WidthPxToPercent(70);
        mVideoPauseRing43.mProgressWidth = CameraPercentUtil.WidthPxToPercent(5);
        mVideoPauseRing43.setProgressRect();
    }

    @Override
    public void AutoAdaptionStaticUI(boolean needAdaption)
    {
        autoAdaptionStaticUI(mVideoPauseRing43, needAdaption);
        autoAdaptionVideoStaticUI(mGifRing, needAdaption);
        autoAdaptionVideoStaticUI(mVideoRing43, needAdaption);
    }

    @Override
    protected void autoAdaptionStaticUI(Ring ring, boolean needAdaption)
    {
        super.autoAdaptionStaticUI(ring, needAdaption);

        int dx = ShareData.m_screenRealHeight - ShareData.getScreenH();
        ring.mAdaptionRecordTextOffsetY = dx > 0 && needAdaption ? dx : 0;
    }

    private void autoAdaptionVideoStaticUI(Ring ring, boolean needAdaption)
    {
        int dx = ShareData.m_screenRealHeight - ShareData.getScreenH();
        if (dx > 0 && needAdaption)
        {
            ring.mAdaptionRecordTextOffsetY = dx;

            // 4:3背景top 那条线在屏幕的位置
            int m_4_3_line_loc = (int) (ShareData.m_screenRealWidth * CameraConfig.PreviewRatio.Ratio_4_3);

            int[] loc = new int[2];
            mView.getLocationOnScreen(loc);

            float radius = ring.mShutterDiameter / 2f;
            float circleCenterY = ring.mCenter.y;

            // 4:3 下圆心位置
            int m_4_3_center_y_loc = m_4_3_line_loc + (ShareData.getScreenH() - m_4_3_line_loc) / 2;
            // 快门圆心在屏幕上的坐标
            int m_current_center_y_loc = (int) (loc[1] + circleCenterY);

            int dy = m_4_3_center_y_loc - m_current_center_y_loc;

            ring.mAdaptionOffsetY = dy > 0 ? dy : 0;

            ring.mAdaptionRadiusScale = 1f;

            if (radius >= (ShareData.getScreenH() - m_4_3_line_loc) / 2)
            {
                // 距离 4：3 白色背景顶部边线最小距离
                int mMinDistanceTo4_3 = CameraPercentUtil.HeightPxToPercent(8);
                // 计算新半径
                int newRadius = m_4_3_center_y_loc - (m_4_3_line_loc + mMinDistanceTo4_3);

                // 超过的话，以 圆心到 4：3背景线为半径
                float scale = newRadius * 1f / radius;
                ring.mAdaptionRadiusScale = scale >= 1 ? 1 : scale;
            }
        }
        else
        {
            ring.mAdaptionRadiusScale = 1f;
            ring.mAdaptionOffsetY = 0;
            ring.mAdaptionRecordTextOffsetY = 0;
        }
    }

    @Override
    public void AutoAdaptionDynamicUI(boolean needAdaption)
    {
        autoAdaptionDynamicUI(mVideoPauseRing43, needAdaption);
        autoAdaptionVideoDynamicUI(mGifRing, needAdaption);
        autoAdaptionVideoDynamicUI(mVideoRing43, needAdaption);
    }

    @Override
    protected void autoAdaptionDynamicUI(Ring ring, boolean needAdaption)
    {
        super.autoAdaptionDynamicUI(ring, needAdaption);

        int dx = ShareData.m_screenRealHeight - ShareData.getScreenH();
        ring.mAdaptionRecordTextOffsetY = dx > 0 && needAdaption ? dx : 0;
    }

    private void autoAdaptionVideoDynamicUI(Ring ring, boolean needAdaption)
    {
        int dx = ShareData.m_screenRealHeight - ShareData.getScreenH();
        if (dx > 0 && needAdaption)
        {
            ring.mAdaptionRecordTextOffsetY = dx;

            // 4:3背景top 那条线在屏幕的位置
            int m_4_3_line_loc = (int) (ShareData.m_screenRealWidth * CameraConfig.PreviewRatio.Ratio_4_3);

            int[] loc = new int[2];
            mView.getLocationOnScreen(loc);

            float radius = ring.mShutterDiameter / 2f;
            float circleCenterY = ring.mCenter.y;

            // 4:3 下圆心位置
            int m_4_3_center_y_loc = m_4_3_line_loc + (ShareData.getScreenH() - m_4_3_line_loc) / 2;
            // 快门圆心在屏幕上的坐标
            int m_current_center_y_loc = (int) (loc[1] + circleCenterY - dx);

            int dy = m_4_3_center_y_loc - m_current_center_y_loc;

            ring.mAdaptionOffsetY = dy > 0 ? dy : 0;

            ring.mAdaptionRadiusScale = 1f;

            if (radius >= (ShareData.getScreenH() - m_4_3_line_loc) / 2)
            {
                // 距离 4：3 白色背景顶部边线最小距离
                int mMinDistanceTo4_3 = CameraPercentUtil.HeightPxToPercent(8);
                // 计算新半径
                int newRadius = m_4_3_center_y_loc - (m_4_3_line_loc + mMinDistanceTo4_3);

                // 超过的话，以 圆心到 4：3背景线为半径
                float scale = newRadius * 1f / radius;
                ring.mAdaptionRadiusScale = scale >= 1 ? 1 : scale;
            }
        }
        else
        {
            ring.mAdaptionRadiusScale = 1f;
            ring.mAdaptionOffsetY = 0;
            ring.mAdaptionRecordTextOffsetY = 0;
        }
    }

    @Override
    protected void initPhotoData()
    {
    }

    @Override
    protected void init169MakeupData()
    {
    }

    @Override
    protected void init43MakeupData()
    {
    }
}
