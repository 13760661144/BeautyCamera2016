package cn.poco.camera3.mgr;

import java.util.ArrayList;

import cn.poco.camera.CameraConfig;
import cn.poco.camera3.config.shutter.ShutterConfig;
import cn.poco.camera3.info.CameraRatioInfo;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * 比例
 * Created by Gxx on 2017/12/6.
 */

public class CameraRatioMgr
{
    private static ArrayList<CameraRatioInfo> mInfoArr;
    private static ArrayList<CameraRatioInfo> mPhotoTypeInfoArr;
    private static float mSelectedRatio;

    private static void InitData()
    {
        float fullScreenRatio = ShareData.m_screenRealHeight * 1.0f / ShareData.m_screenRealWidth;

        if (mInfoArr == null)
        {
            mInfoArr = new ArrayList<>();
        }

        if (mPhotoTypeInfoArr == null)
        {
            mPhotoTypeInfoArr = new ArrayList<>();
        }

        CameraRatioInfo info = new CameraRatioInfo();
        info.setThumb(R.drawable.camera_list_ratio_1_1_gray);
        info.setText("1:1");
        info.setValue(CameraConfig.PreviewRatio.Ratio_1_1);
        mInfoArr.add(info);
        mPhotoTypeInfoArr.add(info);

        info = new CameraRatioInfo();
        info.setThumb(R.drawable.camera_list_ratio_3_4_gray);
        info.setText("3:4");
        info.setValue(CameraConfig.PreviewRatio.Ratio_4_3);
        mInfoArr.add(info);
        mPhotoTypeInfoArr.add(info);

        if (fullScreenRatio >= CameraConfig.PreviewRatio.Ratio_16_9) {
            info = new CameraRatioInfo();
            info.setThumb(R.drawable.camera_list_ratio_9_16_gray);
            info.setValue(CameraConfig.PreviewRatio.Ratio_16_9);
            info.setText("9:16");
            mInfoArr.add(info);
        }

        if (fullScreenRatio > CameraConfig.PreviewRatio.Ratio_16_9) {
            info = new CameraRatioInfo();
            info.setThumb(R.drawable.camera_list_ratio_full_gray);
            info.setValue(CameraConfig.PreviewRatio.Ratio_Full);
            info.setText("全屏");
            mInfoArr.add(info);
        }

        info = new CameraRatioInfo();
        info.setThumb(R.drawable.camera_list_ratio_16_9_gray);
        info.setValue(CameraConfig.PreviewRatio.Ratio_9_16);
        info.setText("16:9");
        mInfoArr.add(info);
    }

    public static boolean updateSelectedStatusByRatio(float ratio)
    {
        if (mSelectedRatio == ratio) return false;

        mSelectedRatio = ratio;

        for (CameraRatioInfo info : mInfoArr)
        {
            if (info != null)
            {
                info.setSelected(info.getValue() == ratio);
            }
        }
        return true;
    }

    public static ArrayList<CameraRatioInfo> getResArr(int tab_type)
    {
        if (mInfoArr == null)
        {
            InitData();
        }

        return tab_type == ShutterConfig.TabType.PHOTO ? mPhotoTypeInfoArr : mInfoArr;
    }

    public static void clearAll()
    {
        if (mInfoArr != null)
        {
            mInfoArr.clear();
            mInfoArr = null;
        }

        if (mPhotoTypeInfoArr != null)
        {
            mPhotoTypeInfoArr.clear();
            mPhotoTypeInfoArr = null;
        }

        mSelectedRatio = 0;
    }
}
