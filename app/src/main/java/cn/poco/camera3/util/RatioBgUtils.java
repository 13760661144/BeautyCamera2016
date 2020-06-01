package cn.poco.camera3.util;

import cn.poco.camera.CameraConfig;
import cn.poco.tianutils.ShareData;

/**
 * @author Gxx
 *         Created by Gxx on 2017/8/15.
 */

public class RatioBgUtils {

    private static float checkRatio(float ratio) {
        if (ratio != 0.0f) {
            if (ratio > CameraConfig.PreviewRatio.Ratio_9_16 - 0.001f && ratio < CameraConfig.PreviewRatio.Ratio_9_16 + 0.001f) {// 608/1080
                ratio = CameraConfig.PreviewRatio.Ratio_9_16;
            }
        }
        return ratio;
    }

    public static int getTopPaddingHeight(float ratio) {
        int out = 0;
        if (ratio <= CameraConfig.PreviewRatio.Ratio_16_9) {
            float fullScreenRatio = ShareData.m_screenRealHeight * 1.0f / ShareData.m_screenRealWidth;
            if (fullScreenRatio > CameraConfig.PreviewRatio.Ratio_17$25_9) {
                out = CameraPercentUtil.HeightPxToPercent(100);
            }
        }
        return out;
    }

    public static int getMaskRealHeight(float ratio) {
        int out = 0;
        if (ratio == CameraConfig.PreviewRatio.Ratio_9_16) {
            //out = Math.round(ShareData.m_screenRealWidth * (CameraConfig.PreviewRatio.Ratio_4_3 - ratio) / 2);
            out = CameraPercentUtil.HeightPxToPercent(100) + Math.round(ShareData.m_screenRealWidth * (CameraConfig.PreviewRatio.Ratio_1_1 - ratio) / 2);

        } else if (ratio == CameraConfig.PreviewRatio.Ratio_1_1) {
            out = CameraPercentUtil.HeightPxToPercent(100);
        }
        return out;
    }

    public static int GetTopHeightByRatio(float ratio) {
        int out = 0;
        ratio = checkRatio(ratio);
        if (ratio != 0.0f && ratio <= CameraConfig.PreviewRatio.Ratio_16_9) {
            out = getTopPaddingHeight(ratio) + getMaskRealHeight(ratio);
        }
        return out;
    }

    public static int GetBottomHeightByRation(float ratio) {
        int out = 0;
        ratio = checkRatio(ratio);
        if (ratio != 0.0f && ratio <= CameraConfig.PreviewRatio.Ratio_16_9) {
            out = Math.round(ShareData.m_screenRealHeight - GetTopHeightByRatio(ratio) - ShareData.m_screenRealWidth * ratio);
        }
        return out;
    }
}
