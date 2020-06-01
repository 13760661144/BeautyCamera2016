package cn.poco.camera3.info.sticker;

import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;

import cn.poco.camera3.mgr.TypeMgr;

/**
 * 标签
 * Created by Gxx on 2017/10/10.
 */

public class LabelInfo
{
    public ArrayList<StickerInfo> mStickerArr; // 素材类型只有 both 、 sticker
    public ArrayList<StickerInfo> mGIFStickerArr; // 素材类型只有 both 、 gif
    public ArrayList<StickerInfo> mSpareStickerArr; // 备用的素材集合

    public static final int BUILT_IN_LABEL_HOT_ID = -10000;
    public static final int BUILT_IN_LABEL_SHAPE_ID = -10001;

    public int ID;
    public int mIndex;
    public String mLabelName;
    public int[] mStickerIDArr;

    @TypeMgr.StickerLabelType
    public int mType = TypeMgr.StickerLabelType.TEXT;

    public boolean isSelected; // 是否被选中
    public boolean isHide; // 是否隐藏标签

    public volatile boolean isShowWhitePoint = false;//显示下方白点
    public volatile boolean isShowRedPoint = false;//显示右上角tips红点

    public void set(LabelInfo info)
    {
        if (info == null) return;
        this.mStickerArr = new ArrayList<>();
        this.mGIFStickerArr = new ArrayList<>();
        this.mSpareStickerArr = new ArrayList<>();
        this.ID = info.ID;
        this.isSelected = info.isSelected;
        this.isHide = info.isHide;
        this.mIndex = info.mIndex;
        this.isShowWhitePoint = info.isShowWhitePoint;
        this.isShowRedPoint = info.isShowRedPoint;
        this.mLabelName = info.mLabelName;
        this.mType = info.mType;
        if (info.mStickerIDArr != null)
        {
            this.mStickerIDArr = ArrayUtils.clone(info.mStickerIDArr);
        }
    }
}
