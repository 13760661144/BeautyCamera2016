package cn.poco.camera3.mgr;

import android.content.Context;
import android.util.SparseIntArray;

import java.util.ArrayList;

import cn.poco.camera.CameraConfig;
import cn.poco.camera3.info.CameraSettingInfo;
import my.beautyCamera.R;

/**
 * @author Gxx
 *         Created by Gxx on 2017/8/21.
 */

public class CameraSettingMgr
{
    private static ArrayList<CameraSettingInfo> mRearFlashArr;
    private static ArrayList<CameraSettingInfo> mTimingArr;
    private static SparseIntArray mGroupSelectedIndexArr;

    public static final int GROUP_MODE_IDLE = 0;
    public static final int GROUP_MODE_REAR_FLASH = 1 << 1;
    public static final int GROUP_MODE_TIMING = 1 << 2;

    private static int mGroupMode = GROUP_MODE_IDLE;

    private static final int REAR_FLASH_SELECTED_INDEX = 1 << 3;
    private static final int TIMING_SELECTED_INDEX = 1 << 4;
    private static int mCurrentTimerMode = CameraConfig.CaptureMode.Manual;

    public static void InitData(Context context)
    {
        if (mGroupSelectedIndexArr == null)
        {
            mGroupSelectedIndexArr = new SparseIntArray();
        }

        if (mRearFlashArr == null)
        {
            mRearFlashArr = new ArrayList<>();

            CameraSettingInfo info = new CameraSettingInfo();
            info.setLogo(R.drawable.camera_flash_open);
            info.setTag(CameraConfig.FlashMode.On);
            info.setText(context.getString(R.string.camera_flash_open));
            mRearFlashArr.add(info);

            info = new CameraSettingInfo();
            info.setLogo(R.drawable.camera_flash_auto);
            info.setTag(CameraConfig.FlashMode.Auto);
            info.setText(context.getString(R.string.camera_flash_auto));
            mRearFlashArr.add(info);

            info = new CameraSettingInfo();
            info.setLogo(R.drawable.camera_flash_close);
            info.setTag(CameraConfig.FlashMode.Off);
            info.setText(context.getString(R.string.camera_flash_close));
            mRearFlashArr.add(info);

            info = new CameraSettingInfo();
            info.setLogo(R.drawable.camera_flash_torch);
            info.setTag(CameraConfig.FlashMode.Torch);
            info.setText(context.getString(R.string.camera_flash_light));
            mRearFlashArr.add(info);

            String flash_mode = CameraConfig.getInstance().getString(CameraConfig.ConfigMap.FlashModeStr);
            int index = CameraSettingInfo.FlashIndex.OFF;
            switch (flash_mode)
            {
                case CameraConfig.FlashMode.On:
                {
                    index = CameraSettingInfo.FlashIndex.ON;
                    break;
                }

                case CameraConfig.FlashMode.Auto:
                {
                    index = CameraSettingInfo.FlashIndex.AUTO;
                    break;
                }

                case CameraConfig.FlashMode.Torch:
                {
                    index = CameraSettingInfo.FlashIndex.TORCH;
                    break;
                }
            }
            info = GetFlashInfoByIndex(index);
            if (info != null)
            {
                info.setIsSelected(true);
            }
            mGroupSelectedIndexArr.put(REAR_FLASH_SELECTED_INDEX, index);
        }

        if (mTimingArr == null)
        {
            mTimingArr = new ArrayList<>();

            CameraSettingInfo info = new CameraSettingInfo();
            info.setLogo(R.drawable.camera_back_setting_timer_off);
            info.setTag(CameraConfig.CaptureMode.Manual);
            info.setText(context.getString(R.string.camerapage_camera_time_take_manual));
            mTimingArr.add(info);

            info = new CameraSettingInfo();
            info.setLogo(R.drawable.camera_back_setting_timer_1s);
            info.setTag(CameraConfig.CaptureMode.Timer_1s);
            info.setText(context.getString(R.string.camerapage_camera_time_take_1));
            mTimingArr.add(info);

            info = new CameraSettingInfo();
            info.setLogo(R.drawable.camera_front_setting_timer_2s);
            info.setTag(CameraConfig.CaptureMode.Timer_2s);
            info.setText(context.getString(R.string.camerapage_camera_time_take_2));
            mTimingArr.add(info);

            info = new CameraSettingInfo();
            info.setLogo(R.drawable.camera_front_setting_timer_10s);
            info.setTag(CameraConfig.CaptureMode.Timer_10s);
            info.setText(context.getString(R.string.camerapage_camera_time_take_10));
            mTimingArr.add(info);
        }
    }

    public static ArrayList<CameraSettingInfo> GetRearFlashGroupData()
    {
        return mRearFlashArr == null ? new ArrayList<CameraSettingInfo>() : mRearFlashArr;
    }

    public static ArrayList<CameraSettingInfo> GetTimingGroupData()
    {
        return mTimingArr == null ? new ArrayList<CameraSettingInfo>() : mTimingArr;
    }

    public static int getSelectedIndex(int group_mode)
    {
        switch (group_mode)
        {
            case GROUP_MODE_REAR_FLASH:
            {
                return mGroupSelectedIndexArr.get(REAR_FLASH_SELECTED_INDEX);
            }

            case GROUP_MODE_TIMING:
            {
                return mGroupSelectedIndexArr.get(TIMING_SELECTED_INDEX);
            }

            default:
            {
                return -1;
            }
        }
    }

    public static CameraSettingInfo getSelectedInfo(int group_mode)
    {
        switch (group_mode)
        {
            case GROUP_MODE_REAR_FLASH:
            {
                int index = 0;
                if (mGroupSelectedIndexArr != null) {
                    index = mGroupSelectedIndexArr.get(REAR_FLASH_SELECTED_INDEX);
                }
                return mRearFlashArr.get(index);
            }

            case GROUP_MODE_TIMING:
            {
                int index = 0;
                if (mGroupSelectedIndexArr != null) {
                    index = mGroupSelectedIndexArr.get(TIMING_SELECTED_INDEX);
                }
                return mTimingArr.get(index);
            }

            default:
            {
                return null;
            }
        }
    }

    public static void setCurrentTimerMode(int mode)
    {
        mCurrentTimerMode = mode;
        int value = CameraSettingInfo.TimingIndex.OFF;
        if(mGroupSelectedIndexArr != null)
        {
            switch(mode)
            {
                case CameraConfig.CaptureMode.Timer_1s:
                {
                    value = CameraSettingInfo.TimingIndex.ONE_SEC;
                    updateSelectedInfo(GROUP_MODE_TIMING, value);
                    break;
                }
                case CameraConfig.CaptureMode.Timer_2s:
                {
                    value = CameraSettingInfo.TimingIndex.TWO_SEC;
                    updateSelectedInfo(GROUP_MODE_TIMING, value);
                    break;
                }
                case CameraConfig.CaptureMode.Timer_10s:
                {
                    value = CameraSettingInfo.TimingIndex.TEN_SEC;
                    updateSelectedInfo(GROUP_MODE_TIMING, value);
                    break;
                }
                default:
                {
                    updateSelectedInfo(GROUP_MODE_TIMING, CameraSettingInfo.TimingIndex.OFF);
                }
            }

            mTimingArr.get(value).setIsSelected(true);
        }
    }

    public static int getCurrentTimerMode()
    {
        return mCurrentTimerMode;
    }

    public static int getGroupMode()
    {
        return mGroupMode;
    }

    public static void updateGroupMode(int mode)
    {
        mGroupMode = mode;
    }

    public static void updateSelectedInfo(int group_mode, int value)
    {
        switch (group_mode)
        {
            case GROUP_MODE_REAR_FLASH:
            {
                mGroupSelectedIndexArr.put(REAR_FLASH_SELECTED_INDEX, value);
                break;
            }

            case GROUP_MODE_TIMING:
            {
                mGroupSelectedIndexArr.put(TIMING_SELECTED_INDEX, value);
                break;
            }
        }
    }

    public static CameraSettingInfo GetTimingInfoByIndex(int index)
    {
        if (mTimingArr != null)
        {
            int size = mTimingArr.size();
            if (index >= 0 && index < size)
            {
                return mTimingArr.get(index);
            }
        }
        return null;
    }

    public static CameraSettingInfo GetFlashInfoByIndex(int index)
    {
        if (mRearFlashArr != null)
        {
            int size = mRearFlashArr.size();
            if (index >= 0 && index < size)
            {
                return mRearFlashArr.get(index);
            }
        }
        return null;
    }

    public static void clearData()
    {
        mGroupMode = GROUP_MODE_IDLE;

        mCurrentTimerMode = CameraConfig.CaptureMode.Manual;

        if (mGroupSelectedIndexArr != null)
        {
            mGroupSelectedIndexArr.clear();
            mGroupSelectedIndexArr = null;
        }

        if (mRearFlashArr != null)
        {
            mRearFlashArr.clear();
            mRearFlashArr = null;
        }

        if (mTimingArr != null)
        {
            mTimingArr.clear();
            mTimingArr = null;
        }
    }
}
