package cn.poco.camera3.ui.sticker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import cn.poco.camera3.cb.sticker.StickerInnerListener;
import cn.poco.camera3.info.sticker.StickerInfo;
import cn.poco.camera3.mgr.StickerResMgr;

/**
 * 贴纸素材
 * Created by Gxx on 2017/10/12.
 */

public class StickerView extends FrameLayout
{
    private ViewPager mStickerViewPager;
    private StickerPagerAdapter mPagerAdapter;
    private StickerInnerListener mHelp;
    private int mSelectedIndex;

    public StickerView(@NonNull Context context)
    {
        super(context);
        initView(context);
    }

    public void ClearAll()
    {
        mHelp = null;

        if (mStickerViewPager != null)
        {
            mStickerViewPager.clearOnPageChangeListeners();
            mStickerViewPager = null;
        }

        if (mPagerAdapter != null)
        {
            mPagerAdapter.setStickerDataHelper(null);
            mPagerAdapter.ClearAll();
            mPagerAdapter = null;
        }

        if (mStickerViewPager != null)
        {
            int size = mStickerViewPager.getChildCount();
            for (int index = 0;index<size;index++)
            {
                View v = mStickerViewPager.getChildAt(index);
                if (v != null && v instanceof StickerPagerView)
                {
                    ((StickerPagerView) v).ClearAll();
                }
            }

            mStickerViewPager.removeAllViews();
        }
    }

    private void initView(Context context)
    {
        mStickerViewPager = new ViewPager(context);
        mStickerViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){}

            @Override
            public void onPageSelected(int position)
            {
                mSelectedIndex = position;

                if (mHelp != null)
                {
                    mHelp.onStickerPageSelected(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
                if(state == ViewPager.SCROLL_STATE_IDLE)
                {
                    smoothToStickerCenter();
                }
            }
        });
        FrameLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.BOTTOM;
        addView(mStickerViewPager, params);

        mPagerAdapter = new StickerPagerAdapter();
        mStickerViewPager.setAdapter(mPagerAdapter);
    }

    public void updateData()
    {
        if (mPagerAdapter != null)
        {
            mPagerAdapter.updatePagerSize(StickerResMgr.getInstance().getLabelArrValidSize());
        }
    }

    public void registerStickerResMgrCB()
    {
        if(mStickerViewPager != null)
        {
            int size = mStickerViewPager.getChildCount();
            for(int index = 0;index < size;index++)
            {
                View child = mStickerViewPager.getChildAt(index);
                if(child != null && child instanceof StickerPagerView)
                {
                    ((StickerPagerView)child).registerStickerResMgrCB();
                }
            }
        }
    }

    public void notifyChildrenUpdateData()
    {
        if (mStickerViewPager != null)
        {
            int size = mStickerViewPager.getChildCount();
            for (int index = 0; index < size; index++)
            {
                View view = mStickerViewPager.getChildAt(index);

                if (view != null && view instanceof StickerPagerView)
                {
                    StickerPagerView children = (StickerPagerView) view;
                    children.updateAdapterData();
                }
            }
        }
    }

    public void smoothToStickerCenter()
    {
        int size = mStickerViewPager.getChildCount();
        for (int index = 0; index < size; index++)
        {
            View view = mStickerViewPager.getChildAt(index);

            if (view != null && view instanceof StickerPagerView)
            {
                StickerPagerView children = (StickerPagerView) view;
                int label_index = children.getLabelIndex();
                if (label_index == StickerResMgr.getInstance().getSelectedLabelIndex())
                {
                    StickerInfo info = StickerResMgr.getInstance().getSelectedStickerInfo();

                    if(info != null)
                    {
                        if(mHelp != null)
                        {
                            mHelp.onShowVolumeBtn(info.mHasMusic);
                        }

                        int sticker_index = StickerResMgr.getInstance().getStickerInfoIndexInPagerView(info.id, label_index);
                        if (sticker_index != -1)
                        {
                            children.smoothScrollToPosition(sticker_index);
                        }
                    }
                }
            }
        }
    }

    public void setStickerDataHelper(StickerInnerListener helper)
    {
        mHelp = helper;
        if (mPagerAdapter != null)
        {
            mPagerAdapter.setStickerDataHelper(helper);
        }
    }

    public void setCurrentItem(int index)
    {
        if (index < 0)
        {
            return;
        }

        if(mSelectedIndex != index)
        {
            if (mPagerAdapter != null && mPagerAdapter.getCount() > 0 && index >= 0 && index < mPagerAdapter.getCount())
            {
                mStickerViewPager.setCurrentItem(index, true);
            }
        }
        else
        {
            mHelp.onLabelScrollToSelected(index);
        }
        smoothToStickerCenter();
    }
}
