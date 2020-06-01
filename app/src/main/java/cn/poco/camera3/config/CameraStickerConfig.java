package cn.poco.camera3.config;

/**
 * 贴纸素材 配置信息
 * Created by Gxx on 2017/10/23.
 */

public class CameraStickerConfig
{

    /**
     * -2：第一个素材
     */
    public static final int STICKER_ID_NORMAL = -2;

    /**
     * -1：无素材
     */
    public static final int STICKER_ID_NON = -1;

    /**
     * -1：默认stickerId所属于下分类
     */
    public static final int STICKER_CATEGORY_ID_NORMAL = -1;

    private Builder mBuilder;

    private CameraStickerConfig(Builder builder)
    {
        mBuilder = builder;
    }

    public boolean isBusiness()
    {
        return mBuilder.mIsBusinessMode;
    }

    public int[] getSpecificLabelArr()
    {
        return mBuilder.mSpecificLabelArr;
    }

    public int getSelectedStickerID()
    {
        return mBuilder.mSelectedStickerID;
    }

    public float getPreviewRatio()
    {
        return mBuilder.mPreviewRatio;
    }

    public int getShutterType()
    {
        return mBuilder.mShutterType;
    }

    public boolean isTailorMadeSetting()
    {
        return mBuilder.mIsTailorMadeSetting;
    }

    public boolean isShowStickerSelector()
    {
        return mBuilder.mIsShowStickerSelector;
    }

    public boolean isAutoRememberUseStickerID()
    {
        return mBuilder.mRememberUseStickerID;
    }

    public int getJustGoToLabelID()
    {
        return mBuilder.mJustGoToLabelID;
    }

    public static class Builder
    {
        private int[] mSpecificLabelArr;
        private int mSelectedStickerID;
        private int mJustGoToLabelID = -1;
        private boolean mIsBusinessMode;
        private int mShutterType;
        private float mPreviewRatio;
        private boolean mIsTailorMadeSetting;//个人中心-美形定制
        private boolean mIsShowStickerSelector;//初始化就显示贴纸列表
        private boolean mRememberUseStickerID = true;

        /**
         * 指定 标签 id
         * @param ids null 默认显示所有非隐藏 label
         */
        public Builder setSpecificLabel(int...ids)
        {
            mSpecificLabelArr =  ids;
            return this;
        }

        public Builder setPreviewRatio(float ratio)
        {
            mPreviewRatio = ratio;
            return this;
        }

        public Builder isRememberUseStickerID(boolean is)
        {
            mRememberUseStickerID = is;
            return this;
        }

        /**
         * 是否只滚到某个分类,默认无贴纸
         * @param labelID
         * @return
         */
        public Builder setJustGoToLabelID(int labelID)
        {
            mJustGoToLabelID = labelID;
            return this;
        }

        /**
         * 指定 贴纸素材 id
         */
        public Builder setSelectedStickerID(int id)
        {
            mSelectedStickerID = id;
            return this;
        }

        /**
         * 是否商业渠道
         */
        public Builder setBusinessMode(boolean is)
        {
            mIsBusinessMode = is;
            return this;
        }

        /**
         * 当前类型
         */
        public Builder setShutterType(int current)
        {
            mShutterType = current;
            return this;
        }

        public Builder setTailorMadeSetting(boolean is)
        {
            this.mIsTailorMadeSetting = is;
            return this;
        }

        /**
         * 是否直接显示贴纸列表
         * @param is
         */
        public void setShowStickerSelector(boolean is)
        {
            this.mIsShowStickerSelector = is;
        }

        public CameraStickerConfig build()
        {
            return new CameraStickerConfig(this);
        }
    }
}
