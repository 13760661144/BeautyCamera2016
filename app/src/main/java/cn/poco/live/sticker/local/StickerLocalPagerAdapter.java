package cn.poco.live.sticker.local;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Created by Gxx on 2017/10/30.
 */

public class StickerLocalPagerAdapter extends PagerAdapter
{
    private int mPagerSize;
    private StickerLocalMgr.DataListener mListener;

    void updatePagerSize(int size)
    {
        mPagerSize = size;
    }

    void setStickerHelper(StickerLocalMgr.DataListener listener)
    {
        mListener = listener;
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
        StickerLocalPagerView itemView = new StickerLocalPagerView(container.getContext(), position);
        itemView.setDataListener(mListener);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        container.addView(itemView, params);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        if (object != null && object instanceof StickerLocalPagerView)
        {
            StickerLocalPagerView view = (StickerLocalPagerView) object;
            view.ClearAll();
        }
        container.removeView((View) object);
    }
}
