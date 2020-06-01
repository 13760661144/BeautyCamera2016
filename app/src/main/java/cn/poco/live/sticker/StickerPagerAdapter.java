package cn.poco.live.sticker;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

/**
 * 直播页
 * Created by Gxx on 2018/1/15.
 */

public class StickerPagerAdapter extends PagerAdapter
{
    private int mSize;
    private StickerMgr.DataListener mListener;

    public void setSize(int size)
    {
        mSize = size;
    }

    void setStickerDataListener(StickerMgr.DataListener listener)
    {
        mListener = listener;
    }

    @Override
    public int getCount()
    {
        return mSize < 0 ? 0: mSize;
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
        itemView.setStickerDataHelper(mListener);
        ViewPager.LayoutParams params = new ViewPager.LayoutParams();
        container.addView(itemView, params);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        if (object != null && object instanceof StickerPagerView)
        {
            ((StickerPagerView) object).ClearAll();
            container.removeView((View) object);
        }
    }
}
