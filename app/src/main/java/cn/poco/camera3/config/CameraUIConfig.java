package cn.poco.camera3.config;

import android.content.Context;
import android.graphics.Color;

import java.util.ArrayList;

import cn.poco.camera.CameraConfig;
import cn.poco.camera3.config.shutter.ShutterConfig;
import cn.poco.camera3.info.TabInfo;
import cn.poco.camera3.util.CameraPercentUtil;
import my.beautyCamera.R;

/**
 * 初始化时，镜头按钮 状态配置信息
 */
public class CameraUIConfig
{
    // btn 状态
    private boolean mIsCameraFront = true; // 是否前置
    private boolean mIsShowTopAdjust = false; // 镜头校正
    private boolean mIsShowBeautyBtn = true; // 美形定制
    private boolean mIsShowTopDirection = true; // 镜头转换
    private boolean mIsShowTopSetting = true; // 设置
    private boolean mIsShowRatio = true;// 比例

    private boolean mIsShowColorFilter = true; // 滤镜 btn
    private boolean mIsShowSticker = true; // 素材 btn
    private boolean mIsShowBackBtn = true; // 返回 btn
    private boolean mIsShowPhoto = true;
    private boolean mIsShowVideoDuration = true;

    // tab 状态
    private int mSelTabType;
    private int mSelTabIndex;
    private int mShutterMode;

    private float mPreviewRatio;

    private ArrayList<TabInfo> mData;
    private TabItemConfig mConfig169;
    private TabItemConfig mConfig43;

    private Object mVideoPauseStatusInfo = null;
    private int mTimerMode;

    private CameraUIConfig(Context context, Builder builder)
    {
        setParams(builder);
        initTabData(context, builder);
    }

    private void setParams(Builder builder)
    {
        this.mIsShowVideoDuration = builder.mIsShowVideoDuration;

        this.mVideoPauseStatusInfo = builder.mVideoPauseStatusInfo;

        this.mIsShowPhoto = builder.mIsShowPhoto;

        this.mIsShowVideoDuration = builder.mIsShowVideoDuration;

        this.mShutterMode = builder.mShutterMode;

        this.mPreviewRatio = builder.mPreviewRatio;

        this.mSelTabType = builder.mSelTabType;

        this.mIsShowBackBtn = builder.mIsShowBack;

        this.mIsCameraFront = builder.mIsCameraFront;

        this.mIsShowTopAdjust = builder.mIsShowTopAdjust;

        this.mIsShowRatio = builder.mIsShowRatio;

        this.mIsShowBeautyBtn = builder.mIsShowBeautyBtn;

        this.mIsShowTopDirection = builder.mIsShowTopDirection;

        this.mIsShowTopSetting = builder.mIsShowTopSetting;

        this.mIsShowColorFilter = builder.mIsShowColorFilter;

        this.mIsShowSticker = builder.mIsShowSticker;

        this.mTimerMode = builder.mTimerMode;
    }

    private void initTabData(Context context, Builder builder)
    {
        mData = new ArrayList<>();
        int itemCount = builder.GetItemCount();

        for (int i = 0; i < itemCount; i++)
        {
            TabInfo info = new TabInfo();
            int type = builder.GetItemTypeByIndex(i);
            switch (type)
            {
                case ShutterConfig.TabType.GIF:
                {
                    info.setInfo(context.getString(R.string.camera_type_gif));
                    info.setTag(ShutterConfig.TabType.GIF);
                    break;
                }

                case ShutterConfig.TabType.PHOTO:
                {
                    info.setInfo(context.getString(R.string.camera_type_photo));
                    info.setTag(ShutterConfig.TabType.PHOTO);
                    break;
                }

                case ShutterConfig.TabType.CUTE:
                {
                    info.setInfo(context.getString(R.string.camera_type_cute));
                    info.setTag(ShutterConfig.TabType.CUTE);
                    break;
                }

                case ShutterConfig.TabType.VIDEO:
                {
                    info.setInfo(context.getString(R.string.camera_type_video));
                    info.setTag(ShutterConfig.TabType.VIDEO);
                }
            }

            if (mSelTabType == type)
            {
                mSelTabIndex = i;
            }

            mData.add(info);
        }

        mConfig169 = new TabItemConfig();
        mConfig169.setShadowLayer(CameraPercentUtil.WidthPxToPercent(5), 0, CameraPercentUtil.HeightPxToPercent(2), 0x4D000000);
        mConfig169.setTextColor(0xffffffff, 0x99ffffff);

        mConfig43 = new TabItemConfig();
        mConfig43.setShadowLayer(1, 0, 0, Color.TRANSPARENT);
        mConfig43.setTextColor(0xff000000, 0x99000000);
    }

    public TabItemConfig GetTabItemConfig169()
    {
        return mConfig169;
    }

    public TabItemConfig GetTabItemConfig43()
    {
        return mConfig43;
    }

    public float GetPreviewRatio()
    {
        return mPreviewRatio;
    }

    public int GetSelectedType()
    {
        return mSelTabType;
    }

    public int GetShutterMode()
    {
        return mShutterMode;
    }

    public int GetSelectedIndex()
    {
        return mSelTabIndex;
    }

    public boolean isShowBackBtn()
    {
        return mIsShowBackBtn;
    }

    public boolean isShowPatchBtn()
    {
        return mIsShowTopAdjust;
    }

    public boolean isShowBeautyBtn()
    {
        return mIsShowBeautyBtn;
    }

    public boolean isShowSwitchBtn()
    {
        return mIsShowTopDirection;
    }

    public boolean isShowSettingBtn()
    {
        return mIsShowTopSetting;
    }

    public boolean isShowRatioBtn()
    {
        return mIsShowRatio;
    }

    public boolean isShowFilterBtn()
    {
        return mIsShowColorFilter;
    }

    public boolean isCameraFrontMode()
    {
        return mIsCameraFront;
    }

    public boolean isShowStickerBtn()
    {
        return mIsShowSticker;
    }

    public boolean isShowPhoto()
    {
        return mIsShowPhoto;
    }

    public boolean isShowVideoDuration()
    {
        return mIsShowVideoDuration;
    }

    public ArrayList<TabInfo> GetTabData()
    {
        return mData;
    }

    public int GetTabSize()
    {
        return mData != null ? mData.size() : -1;
    }

    public Object GetResetVideoPauseStatusInfo()
    {
        return mVideoPauseStatusInfo;
    }

    public void ClearMemory()
    {
        if (mData != null)
        {
            mData.clear();
            mData = null;
        }

        if (mConfig169 != null)
        {
            mConfig169 = null;
        }

        if (mConfig43 != null)
        {
            mConfig43 = null;
        }
    }

    public int getTimerMode()
    {
        return mTimerMode;
    }

    public static class Builder
    {
        // bottom control btn
        private boolean mIsShowBack = true; // 返回 btn
        private boolean mIsShowColorFilter = true; // 滤镜 btn
        private boolean mIsShowSticker = true; // 素材 btn
        private boolean mIsShowPhoto = true;
        private boolean mIsShowVideoDuration = true; // 是否显示录制时长
        private boolean mIsShowBeautyBtn = true; // 美形定制

        // top control btn
        private boolean mIsCameraFront = true; // 是否前置
        private boolean mIsShowTopAdjust = false; // 镜头校正
        private boolean mIsShowTopDirection = true; // 镜头转换
        private boolean mIsShowTopSetting = true; // 设置
        private boolean mIsShowRatio = true;// 比例

        private ArrayList<Integer> mTabTypeList;

        @ShutterConfig.TabType
        private int mSelTabType = ShutterConfig.TabType.PHOTO;

        @ShutterConfig.ShutterType
        private int mShutterMode = ShutterConfig.ShutterType.DEF;

        private int mTypeStatus = 0;

        private int mTopTypeStatus = CameraBtnConfig.SHOW_ALL_TOP_BAR_BTN;

        private float mPreviewRatio = CameraConfig.PreviewRatio.Ratio_4_3;

        private Object mVideoPauseStatusInfo = null;

        private int mVideoDurationType = ShutterConfig.ALL_VIDEO_DURATION_TYPE;
        private int mTimerMode = CameraConfig.CaptureMode.Manual;

        public Builder()
        {
            mTabTypeList = new ArrayList<>();
        }

        public static CameraUIConfig GetDefConfig(Context context)
        {
            return new Builder().setShowTabType(ShutterConfig.ALL_TYPE).build(context);
        }

        public static CameraUIConfig GetLessThan18Config(Context context)
        {
            return new Builder().setShowTabType(ShutterConfig.TabType.PHOTO).build(context);
        }

        /**
         * 设置 tab 类型
         *
         * @param type 参考:{@link ShutterConfig#ALL_TYPE} <br/>
         *             or {@link cn.poco.camera3.config.shutter.ShutterConfig.TabType}
         * @return builder
         */
        public Builder setShowTabType(int type)
        {
            mTypeStatus |= type;
            return this;
        }

        public Builder setShutterMode(int mode)
        {
            mShutterMode = mode;
            return this;
        }

        public Builder setCameraPreviewRatio(float ratio)
        {
            mPreviewRatio = ratio;
            return this;
        }

        public Builder isCameraFront(boolean front)
        {
            mIsCameraFront = front;
            return this;
        }

        public Builder isShowBack(boolean show)
        {
            mIsShowBack = show;
            return this;
        }

        public Builder setShowTopType(int type)
        {
            mTopTypeStatus |= type;
            return this;
        }

        public Builder setHideTopType(int type)
        {
            mTopTypeStatus &= ~type;
            return this;
        }

        public Builder isShowBeauty(boolean show)
        {
            mIsShowBeautyBtn = show;
            return this;
        }

        public Builder isShowColorFilter(boolean show)
        {
            mIsShowColorFilter = show;
            return this;
        }

        public Builder isShowSticker(boolean show)
        {
            mIsShowSticker = show;
            return this;
        }

        public Builder SelectedTabType(int type)
        {
            mSelTabType = type;
            return this;
        }

        public Builder setIsShowPhoto(boolean show)
        {
            this.mIsShowPhoto = show;
            return this;
        }

        public Builder setTimerMode(int mode)
        {
            this.mTimerMode = mode;
            return this;
        }

        /**
         * 设置 视频 时长 类型
         * @param type
         * @return
         */
        public Builder setVideoDurationType(@ShutterConfig.VideoDurationType int type)
        {
            if (mVideoDurationType == ShutterConfig.ALL_VIDEO_DURATION_TYPE)
            {
                mVideoDurationType &= ShutterConfig.NO_TYPE;
            }
            mVideoDurationType |= type;
            return this;
        }

        private int GetItemCount()
        {
            return mTabTypeList.size();
        }

        private int GetItemTypeByIndex(int index)
        {
            return index < 0 || index >= GetItemCount() ? -1 : mTabTypeList.get(index);
        }

        private void initTabData()
        {
            if (containTabType(ShutterConfig.TabType.GIF))
            {
                mTabTypeList.add(ShutterConfig.TabType.GIF);
            }

            if (containTabType(ShutterConfig.TabType.PHOTO))
            {
                mTabTypeList.add(ShutterConfig.TabType.PHOTO);
            }

            if (containTabType(ShutterConfig.TabType.CUTE))
            {
                mTabTypeList.add(ShutterConfig.TabType.CUTE);
            }

            if (containTabType(ShutterConfig.TabType.VIDEO))
            {
                mTabTypeList.add(ShutterConfig.TabType.VIDEO);
            }

            // 默认 type
            if (mTabTypeList.size() == 0)
            {
                mTabTypeList.add(ShutterConfig.TabType.PHOTO);
            }

            // 判断选中的type 是否包含在 已选type 当中
            if (!containTabType(mSelTabType))
            {
                mSelTabType = mTabTypeList.get(0);
            }
        }

        private void initTopData()
        {
            mIsShowRatio = containTopType(CameraBtnConfig.BarType.CAMERA_RATIO);

            mIsShowTopAdjust = containTopType(CameraBtnConfig.BarType.CAMERA_ADJUST);

            mIsShowTopSetting = containTopType(CameraBtnConfig.BarType.CAMERA_SETTING);

            mIsShowTopDirection = containTopType(CameraBtnConfig.BarType.CAMERA_DIRECTION);
        }

        private boolean containTabType(int type)
        {
            return (mTypeStatus & type) != 0;
        }

        private boolean containTopType(int type)
        {
            return (mTopTypeStatus & type) != 0;
        }

        public Builder resumeVideoPauseStatus(Object info)
        {
            mVideoPauseStatusInfo = info;
            return this;
        }

        public CameraUIConfig build(Context context)
        {
            initTabData();
            initTopData();
            initDurationData();

            return new CameraUIConfig(context, this);
        }

        private void initDurationData()
        {
            int out = 0;
            if ((mVideoDurationType & ShutterConfig.VideoDurationType.DURATION_TEN_SEC) != 0)
            {
                out += 1;
            }

            if ((mVideoDurationType & ShutterConfig.VideoDurationType.DURATION_THREE_MIN) != 0)
            {
                out += 1;
            }

            if (out <= 1) mIsShowVideoDuration = false;
        }
    }
}
