/*
 * Copyright (C) 2011 The Android Open Source Project
 * Copyright (C) 2011 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.poco.camera3.beauty.viewpagerIndicator;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.Size;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.camera3.util.CameraPercentUtil;
import cn.poco.tianutils.ShareData;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * This widget implements the dynamic action bar tab behavior that can change
 * across different configurations or circumstances.
 */
public class TabPageIndicator extends HorizontalScrollView implements PageIndicator
{
    /**
     * Title text used when no title is provided by the adapter.
     */
    private static final CharSequence EMPTY_TITLE = "";

    public interface OnTabSelectListener
    {
        void onTabSelected(int oldPosition, int newPosition);
    }

    public interface OnPageChangeListenerEx extends OnPageChangeListener
    {
        void onInitialPage(int position);
    }

    private Runnable mTabSelector;

    private final OnClickListener mTabClickListener = new OnClickListener()
    {
        public void onClick(View view)
        {
            TabViewEx tabView = (TabViewEx) view;
            final int oldSelected = mViewPager.getCurrentItem();
            final int newSelected = tabView.getIndex();
            mViewPager.setCurrentItem(newSelected);
            if (mTabselectListener != null)
            {
                mTabselectListener.onTabSelected(oldSelected, newSelected);
            }
        }
    };

    private final IcsLinearLayout mTabLayout;

    private ViewPager mViewPager;
    private OnPageChangeListenerEx mListener;

    private int mMaxTabWidth;
    private int mSelectedTabIndex;

    private OnTabSelectListener mTabselectListener;

    public TabPageIndicator(Context context)
    {
        this(context, null);
    }

    public TabPageIndicator(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setHorizontalScrollBarEnabled(false);

        mTabLayout = new IcsLinearLayout(context);
        addView(mTabLayout, new ViewGroup.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
    }

    public void setOnTabSelectListener(OnTabSelectListener listener)
    {
        mTabselectListener = listener;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final boolean lockedExpanded = widthMode == MeasureSpec.EXACTLY;
        setFillViewport(lockedExpanded);

        final int childCount = mTabLayout.getChildCount();
        if (childCount > 1 && (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST))
        {
            if (childCount > 2)
            {
                mMaxTabWidth = (int) (MeasureSpec.getSize(widthMeasureSpec) * 0.4f);
            }
            else
            {
                mMaxTabWidth = MeasureSpec.getSize(widthMeasureSpec) / 2;
            }
        }
        else
        {
            mMaxTabWidth = -1;
        }

        final int oldWidth = getMeasuredWidth();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int newWidth = getMeasuredWidth();

        if (lockedExpanded && oldWidth != newWidth)
        {
            // Recenter the tab display if we're at a new (scrollable) size.
            setCurrentItem(mSelectedTabIndex);
        }
    }

    private void animateToTab(final int position)
    {
        final View tabView = mTabLayout.getChildAt(position);
        if (mTabSelector != null)
        {
            removeCallbacks(mTabSelector);
        }
        mTabSelector = new Runnable()
        {
            public void run()
            {
                final int scrollPos = tabView.getLeft() - (getWidth() - tabView.getWidth()) / 2;
                smoothScrollTo(scrollPos, 0);
                mTabSelector = null;
            }
        };
        post(mTabSelector);
    }

    @Override
    public void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (mTabSelector != null)
        {
            // Re-post the selector we saved
            post(mTabSelector);
        }
    }

    @Override
    public void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        if (mTabSelector != null)
        {
            removeCallbacks(mTabSelector);
        }
    }

    private TabViewEx newTab(int index, CharSequence text, @Size(2) int[] iconResId)
    {
        final TabViewEx tabView = new TabViewEx(getContext());
        tabView.mIndex = index;
        tabView.setFocusable(true);
        tabView.setIcon(iconResId[0], iconResId[1]);
        tabView.setOnClickListener(mTabClickListener);
        tabView.mTitle.setText(text);
        return tabView;
    }

    private LinearLayout.LayoutParams createLinearLayoutParams(int count, int position)
    {
        //超过3个或一个tab等比
        LinearLayout.LayoutParams layoutParams;
        if (count >= 3 || count <= 1) {
            layoutParams = new LinearLayout.LayoutParams(0, MATCH_PARENT);
            layoutParams.weight = 1;
        } else {
            int width = CameraPercentUtil.WidthPxToPercent(66 + 180);
            layoutParams = new LinearLayout.LayoutParams(width, MATCH_PARENT);
            int margin = (ShareData.m_screenRealWidth - (width * 2)) / 2;
            if (position % 2 == 0)
            {
                layoutParams.leftMargin = margin;
            } else {
                layoutParams.rightMargin = margin;
            }
        }
        return layoutParams;
    }

    @Override
    public void onPageScrollStateChanged(int arg0)
    {
        if (mListener != null)
        {
            mListener.onPageScrollStateChanged(arg0);
        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2)
    {
        if (mListener != null)
        {
            mListener.onPageScrolled(arg0, arg1, arg2);
        }
    }

    @Override
    public void onPageSelected(int arg0)
    {
        setCurrentItem(arg0);
        if (mListener != null)
        {
            mListener.onPageSelected(arg0);
        }
    }

    @Override
    public void setViewPager(ViewPager view)
    {
        if (mViewPager == view)
        {
            return;
        }
        if (mViewPager != null)
        {
            mViewPager.removeOnPageChangeListener(null);
        }
        final PagerAdapter adapter = view.getAdapter();
        if (adapter == null)
        {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        mViewPager = view;
        view.addOnPageChangeListener(this);
        notifyDataSetChanged();
        if (mListener != null)
        {
            mListener.onInitialPage(mSelectedTabIndex);
        }
    }

    public void notifyDataSetChanged()
    {
        mTabLayout.removeAllViews();
        PagerAdapter adapter = mViewPager.getAdapter();
        IconPagerAdapter iconAdapter = null;
        if (adapter instanceof IconPagerAdapter)
        {
            iconAdapter = (IconPagerAdapter) adapter;
        }
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++)
        {
            CharSequence title = adapter.getPageTitle(i);
            if (title == null)
            {
                title = EMPTY_TITLE;
            }

            int[] iconResId = new int[2];
            if (iconAdapter != null)
            {
                iconResId = iconAdapter.getIconResId(i);
            }
            TabViewEx tabViewEx = newTab(i, title, iconResId);
            mTabLayout.addView(tabViewEx, createLinearLayoutParams(count, i));
        }
        if (mSelectedTabIndex > count)
        {
            mSelectedTabIndex = count - 1;
        }
        setCurrentItem(mSelectedTabIndex);
        requestLayout();
    }

    @Override
    public void setViewPager(ViewPager view, int initialPosition)
    {
        setViewPager(view);
        setCurrentItem(initialPosition);
    }

    @Override
    public void setCurrentItem(int item)
    {
        if (mViewPager == null)
        {
            throw new IllegalStateException("ViewPager has not been bound.");
        }
        mSelectedTabIndex = item;
        mViewPager.setCurrentItem(item);
        final int tabCount = mTabLayout.getChildCount();
        for (int i = 0; i < tabCount; i++)
        {
            final View child = mTabLayout.getChildAt(i);
            final boolean isSelected = (i == item);

            child.setSelected(isSelected);
            if (isSelected)
            {
                animateToTab(item);
            }
        }
    }

    public void setOnPageChangeExListener(OnPageChangeListenerEx listener)
    {
        mListener = listener;
    }

    private class TabViewEx extends RelativeLayout
    {
        private int mIndex;
        private TextView mTitle;
        private ImageView mIcon;
        private View mLine;

        private int mIconSelect;
        private int mIconUnSelect;

        public TabViewEx(Context context)
        {
            super(context);
            initView();
        }

        private void initView()
        {
            LinearLayout container = new LinearLayout(getContext());
            container.setOrientation(LinearLayout.HORIZONTAL);
            container.setGravity(Gravity.CENTER);
            RelativeLayout.LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            this.addView(container, params);
            {
                mIcon = new ImageView(getContext());
                mIcon.setId(android.R.id.icon);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER;
                container.addView(mIcon, layoutParams);

                mTitle = new TextView(getContext());
                mTitle.setId(android.R.id.text1);
                mTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f);
                layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER;
                layoutParams.leftMargin = CameraPercentUtil.WidthPxToPercent(1);
                container.addView(mTitle, layoutParams);
            }

            mLine = new View(getContext());
            params = new LayoutParams(CameraPercentUtil.WidthPxToPercent(180), CameraPercentUtil.HeightPxToPercent(4));
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            this.addView(mLine, params);

        }

        public void setIcon(int select, int unSelect)
        {
            mIconSelect = select;
            mIconUnSelect = unSelect;
        }

        @Override
        public void setSelected(boolean selected)
        {
            super.setSelected(selected);
            if (mIconSelect != 0 || mIconUnSelect != 0)
            {
                mIcon.setImageResource(selected ? mIconSelect : mIconUnSelect);
                if (selected) {
                    mIcon.setColorFilter(ImageUtils.GetSkinColor(), PorterDuff.Mode.SRC_IN);
                } else {
                    mIcon.clearColorFilter();
                }
            }
            mLine.setBackgroundColor(selected ? ImageUtils.GetSkinColor() : Color.TRANSPARENT);
            mTitle.setTextColor(selected ? ImageUtils.GetSkinColor() : 0xff737373);
        }

        @Override
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
        {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            // Re-measure if we went beyond our maximum size.
            if (mMaxTabWidth > 0 && getMeasuredWidth() > mMaxTabWidth)
            {
                super.onMeasure(MeasureSpec.makeMeasureSpec(mMaxTabWidth, MeasureSpec.EXACTLY),
                        heightMeasureSpec);
            }
        }

        public int getIndex()
        {
            return mIndex;
        }
    }
}
