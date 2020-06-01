package cn.poco.camera3.info.sticker;

import java.util.ArrayList;

import cn.poco.camera3.mgr.TypeMgr;

/**
 * @author Created by Gxx on 2017/11/2.
 */

public class LabelLocalInfo
{
    public ArrayList<StickerInfo> mStickerArr;

    public int ID;
    public int mIndex;
    public String mLabelName;

    @TypeMgr.StickerLabelType
    public int mType = TypeMgr.StickerLabelType.TEXT;

    public boolean isSelected; // 是否被选中
}
