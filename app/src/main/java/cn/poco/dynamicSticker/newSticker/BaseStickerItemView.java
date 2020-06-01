package cn.poco.dynamicSticker.newSticker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.FrameLayout;

/**
 * @author lmx
 *         Created by lmx on 2017/6/28.
 */
public abstract class BaseStickerItemView extends FrameLayout
{
    protected boolean mIsSelected;
    protected int position;

    public BaseStickerItemView(@NonNull Context context)
    {
        super(context);
        initData();
        initView();
    }

    public abstract void initData();

    public abstract void initView();

    public abstract void setDownloadProgress(int progress, boolean reset);

    public abstract void setThumb(Object thumb);

    public abstract void setThumb(Object thumb, boolean isDownloaded);

    public void setPosition(int position)
    {
        this.position = position;
    }

    public int getPosition()
    {
        return position;
    }

    public boolean isSelected()
    {
        return mIsSelected;
    }

    public void setIsSelected(boolean isSelected)
    {
        this.mIsSelected = isSelected;
    }

    public void clear()
    {

    }
}
