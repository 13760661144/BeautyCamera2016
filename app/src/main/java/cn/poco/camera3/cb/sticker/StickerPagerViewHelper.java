package cn.poco.camera3.cb.sticker;

/**
 * 贴纸素材回调
 * Created by Gxx on 2017/10/11.
 */

public abstract class StickerPagerViewHelper
{
    private int mIndex;

    public StickerPagerViewHelper(int index)
    {
        mIndex = index;
    }

    public int getIndex()
    {
        return mIndex;
    }

    public abstract void OnProgress(int stickerID);

    public abstract void OnComplete(int stickerID);

    public abstract void OnFail(int stickerID);

    public abstract void onDataChange(int stickerID);
}
