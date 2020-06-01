package cn.poco.camera3.ui.tab;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;

public final class HorizontalScrollLayout extends HorizontalScrollView
{
    private TabView mView;
    private boolean mUIEnable = true;

    public HorizontalScrollLayout(@NonNull Context context)
    {
        super(context);
        setHorizontalFadingEdgeEnabled(false);
        // 去除滑到尽头时的阴影
        setOverScrollMode(View.OVER_SCROLL_NEVER);
        // 水平方向的水平滚动条是否显示
        setHorizontalScrollBarEnabled(false);

        initView();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        if (!mUIEnable) return true;

        if (getChildCount() > 0)
        {
            View view = getChildAt(0);
            if (view != null)
            {
                view.dispatchTouchEvent(ev);
            }
        }
        return true;
    }

    private void initView()
    {
        mView = new TabView(getContext());
        setFillViewport(true);
        addView(mView);
    }

    public void SetUIEnable(boolean enable)
    {
        mUIEnable = enable;
    }

    public boolean isScrollStated()
    {
        return mView.isScrollStarted();
    }

    public void SetAdapter(@NonNull TabViewBaseAdapter adapter)
    {
        mView.setAdapter(adapter);
    }

    public void SetOnItemScrollStateListener(TabView.OnItemScrollStateListener listener)
    {
        mView.SetOnItemScrollStateListener(listener);
    }

    public int GetCurrentSelectedIndex()
    {
        return mView.GetCurrSelIndex();
    }

    public void SmoothToPosition(int position)
    {
        mView.SmoothToPosition(position);
    }

    public void SetOriginallySelectedIndex(int index)
    {
        mView.SetOriginallySelectedIndex(index);
    }

    public void SelectCurrentIndex(int index)
    {
        mView.SelectCurrentIndex(index);
    }

    public PointF getStickerLogoCenter()
    {
        return mView.getStickerLogoCenter();
    }

    public void ClearMemory()
    {
        mView.ClearMemory();
        removeAllViews();
    }
}
