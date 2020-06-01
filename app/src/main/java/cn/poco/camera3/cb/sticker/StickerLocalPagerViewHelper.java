package cn.poco.camera3.cb.sticker;

/**
 *
 * Created by Gxx on 2017/10/30.
 */

public abstract class StickerLocalPagerViewHelper
{
    private int mIndex;

    public StickerLocalPagerViewHelper(int index)
    {
        mIndex = index;
    }

    public int getIndex()
    {
        return mIndex;
    }

    public abstract void OnAllDataChange();

    public abstract void OnDataChange(int sticker_id);
}
