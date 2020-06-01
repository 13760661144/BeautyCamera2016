package cn.poco.camera3.ui.sticker;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import cn.poco.camera3.cb.sticker.StickerInnerListener;

/**
 * 贴纸
 * Created by Gxx on 2017/10/12.
 */

class StickerPagerAdapter extends PagerAdapter
{
    private int mPagerSize;
    private StickerInnerListener mHelp;

    void updatePagerSize(int size)
    {
        mPagerSize = size;
        notifyDataSetChanged();
    }

    public void ClearAll()
    {
        mHelp = null;
    }

    void setStickerDataHelper(StickerInnerListener helper)
    {
        mHelp = helper;
    }

    @Override
    public int getCount()
    {
        return mPagerSize;
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        StickerPagerView itemView = new StickerPagerView(container.getContext(), position);
        itemView.setStickerDataHelper(mHelp);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        container.addView(itemView, params);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        if (object != null && object instanceof StickerPagerView)
        {
            StickerPagerView view = (StickerPagerView) object;
            view.ClearAll();
        }
        container.removeView((View) object);
    }
}
