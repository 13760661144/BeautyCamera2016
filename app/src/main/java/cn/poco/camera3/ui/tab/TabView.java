package cn.poco.camera3.ui.tab;

import android.content.Context;
import android.graphics.PointF;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.Scroller;

import cn.poco.camera3.config.shutter.ShutterConfig;
import cn.poco.camera3.info.TabInfo;
import cn.poco.tianutils.ShareData;

/**
 * 支持水平滑动、手指up的时候自动选中item
 */

public class TabView extends LinearLayout
{
    public interface OnItemScrollStateListener
    {
        void onFingerMove();

        // 成功选中与当前 selIndex 不同的 item 时回调, 同时滑动结束
        void onItemSelected(int currSelIndex, int type);

        // 发生 move 事件回调
        void onScrollStart();

        // down 和 up 都是同一个item 时回调
        void onScrollEnd();

        // 低于18sdk时，滚动或选中 gif 、 视频的回调
        void onLessThan18SDK(int type);
    }

    private OnItemScrollStateListener mItemScrollStateListener;

    public void SetOnItemScrollStateListener(OnItemScrollStateListener listener)
    {
        mItemScrollStateListener = listener;
    }

    //*********************************************************************************************************//

    private Scroller mScroller;

    // 选中区域的中心X
    private int mSelAreaMidX;

    // 计算move事件滑动距离
    private float mXDown;
    private float mXLastMove;
    private float mXMove;

    private boolean mIsToRight = false; // 手指滑动方向
    private boolean mUseScroller = false;
    private boolean mUIEnable = true;
    private boolean mIsScrollStart = false;// 是否开始滑动

    // 计算down --> up 过程滑动速率
    private long mDownMoment;
    private float mDownX, mVelocityX;

    // 判定为拖动的最小移动像素数
    private int mTouchSlop;

    // 用于记录滑动过程index
    private int mTargetIndex;
    private int mTempIndex;

    // 用于记录初始化过程要选中的index
    private int mInitSelIndex;

    // 用于记录滑动结束index
    private int mSelIndex;

    private boolean mFinishMeasure = false;

    private boolean mIsFingerDown;

    private boolean mIsMoreThan18SDK = true;

    public TabView(Context context)
    {
        super(context);

        mIsMoreThan18SDK = Build.VERSION.SDK_INT >= 18;

        mScroller = new Scroller(context);

        // 获取TouchSlop值
        mTouchSlop = ViewConfiguration.get(context).getScaledPagingTouchSlop();
    }

    public void ClearMemory()
    {
        getViewTreeObserver().removeOnPreDrawListener(mPreDrawListener);
        mPreDrawListener = null;

        mItemScrollStateListener = null;
        mScroller = null;

        if (mAdapter != null)
        {
            mAdapter.ClearMemory();
            mAdapter = null;
        }

        int count = getChildCount();
        for (int i = 0; i < count; i++)
        {
            View view = getChildAt(i);
            if (view != null)
            {
                view.setOnTouchListener(null);
                view.setOnClickListener(null);
                view.setOnLongClickListener(null);
            }

            if (view instanceof ViewGroup)
            {
                ViewGroup vg = (ViewGroup) view;
                int childCount = vg.getChildCount();
                for (int j = 0; j < childCount; j++)
                {
                    view = vg.getChildAt(j);
                    if (view != null)
                    {
                        view.setOnTouchListener(null);
                        view.setOnClickListener(null);
                        view.setOnLongClickListener(null);
                    }
                }
                vg.removeAllViews();
            }
        }
        removeAllViews();
    }

    @Override
    public void setOrientation(int orientation)
    {
        orientation = HORIZONTAL;
        super.setOrientation(orientation);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        mSelAreaMidX = (w >= ShareData.m_screenWidth) ? (ShareData.m_screenWidth / 2) : (getLeft() + w / 2);

        int size = getChildCount();

        mSelIndex = (size > 0) && (mInitSelIndex < 0 || mInitSelIndex >= size) ? 0 : mInitSelIndex;
        mInitSelIndex = -1;

        int sumWidth = 0;
        int width;
        for (int i = 0; i <= mSelIndex; i++)
        {
            View child = getChildAt(i);

            if (child == null) continue;

            width = child.getMeasuredWidth();

            if (i == mSelIndex)
            {
                int dx = -getScrollX() + sumWidth + child.getMeasuredWidth() / 2 - mSelAreaMidX;
                scrollTo(dx, 0);
                mIsScrollTo = true;
            }
            else
            {
                sumWidth += width;
            }
        }

        mFinishMeasure = true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        if (!canScrollChildren())
        {
            return super.dispatchTouchEvent(ev);
        }

        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                mVelocityX = 0;
                mDownX = ev.getRawX();
                mIsFingerDown = true;
                mDownMoment = System.currentTimeMillis();
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                if (ev.getY() < 0) {
                    mVelocityX = 0;
                } else {
                    mVelocityX = (ev.getX() - mDownX) / (System.currentTimeMillis() - mDownMoment) * 1000f;
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if (!canScrollChildren())
        {
            return false;
        }

        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                mXDown = ev.getRawX();
                mXLastMove = mXDown;
                break;
            }
            case MotionEvent.ACTION_MOVE:
            {
                mXMove = ev.getRawX();
                float diff = Math.abs(mXMove - mXDown);
                mXLastMove = mXMove;
                // 当手指拖动值大于TouchSlop值时，认为应该进行滚动，拦截子控件的事件
                if (diff > mTouchSlop)
                {
                    return true;
                }
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        if (!mUIEnable && !mIsFingerDown)
        {
            return true;
        }

        if (!canScrollChildren())
        {
            return false;
        }

        switch (ev.getAction())
        {
            case MotionEvent.ACTION_MOVE:
            {
                if (ev.getY() < 0) {
                    return false;
                }
                mXMove = ev.getRawX();
                int scrolledX = (int) (mXLastMove - mXMove);

                if (scrolledX == 0)
                {
                    break;
                }

                if (scrolledX < 0)
                {
                    mIsToRight = true;

                    int loc = getViewLocation(getChildAt(0));
                    int dx = loc - mSelAreaMidX;
                    if (dx >= 0 || dx - scrolledX >= 0)
                    {
                        scrollBy(dx, 0);
                    }
                    else
                    {
                        scrollBy(scrolledX, 0);
                    }
                }
                else
                {
                    mIsToRight = false;

                    int loc = getViewLocation(getChildAt(getChildCount() - 1));
                    int dx = loc - mSelAreaMidX;
                    if (dx <= 0 || dx - scrolledX <= 0)
                    {
                        scrollBy(dx, 0);
                    }
                    else
                    {
                        scrollBy(scrolledX, 0);
                    }
                }

                mXLastMove = mXMove;
                if (!mIsScrollStart)
                {
                    mIsScrollStart = true;
                    if (mItemScrollStateListener != null)
                    {
                        mItemScrollStateListener.onFingerMove();
                    }
                }
                break;
            }

            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_UP:
            {
                int dx;
                int minDX = 0; // 记录item 与选中区域中心X 的最小差值
                int index = 0;

                int childCount = getChildCount();

                for (int i = 0; i < childCount; i++)
                {
                    View child = getChildAt(i);

					/*
                    1. 先计算每个子view 中心 与选中区域中心 mMidX 的差值 dx
					2. 再与上一次记录下的 dX 比较，保留绝对值小的index
						2.1 若 绝对值相等，根据 mIsToRight 判断 --> true：保留index小的
					3. 如果index跟上一次一样，根据速率 mVelocityX (每秒划过多少像素) 判断是否要选中下一个item
					4. 滚动
					 */
                    int childMidX = getViewLocation(child);
                    dx = Math.abs(childMidX - mSelAreaMidX);
                    if (i == 0)
                    {
                        minDX = dx;
                        index = 0;
                    }
                    else if (minDX >= dx)
                    {
                        minDX = dx;
                        index = i;
                    }
                }

                // 处理两个子 View 都可能符合滚动到中间的情况
                if (index > 0)
                {
                    View exView = getChildAt(index - 1);
                    int exViewMidX = getViewLocation(exView);

                    View view = getChildAt(index);
                    int viewMidX = getViewLocation(view);

                    int exD = Math.abs(exViewMidX - mSelAreaMidX);
                    int d = Math.abs(viewMidX - mSelAreaMidX);
                    if (d == exD && mIsToRight)// 手指右移
                    {
                        index -= 1;
                    }
                }

                if (index == mSelIndex)
                {
                    if (mVelocityX > ShareData.PxToDpi_xhdpi(50) && mSelIndex > 0)
                    {
                        index = mSelIndex - 1;
                    }
                    else if (mVelocityX < -ShareData.PxToDpi_xhdpi(50) && mSelIndex < getChildCount() - 1)
                    {
                        index = mSelIndex + 1;
                    }
                }

                if (mIsMoreThan18SDK)
                {
                    AutoSelItem(index);
                }
                else if (mAdapter != null)
                {
                    TabInfo info = mAdapter.getTabInfoByIndex(index);
                    if (info != null)
                    {
                        int type = (int) info.getTag();

                        switch (type)
                        {
                            case ShutterConfig.TabType.GIF:
                            case ShutterConfig.TabType.VIDEO:
                            {
                                resetPosition();
                                if (mItemScrollStateListener != null)
                                {
                                    mItemScrollStateListener.onLessThan18SDK(type);
                                }
                                break;
                            }

                            case ShutterConfig.TabType.CUTE:
                            case ShutterConfig.TabType.PHOTO:
                            {
                                AutoSelItem(index);
                                break;
                            }
                        }
                    }
                }

                mIsFingerDown = false;
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            {
                resetPosition();
                mIsFingerDown = false;
            }
        }
        super.onTouchEvent(ev);
        return true;
    }

    private int getViewLocation(View child)
    {
        return -getScrollX() + child.getLeft() + child.getMeasuredWidth() / 2;
    }

    @Override
    public void computeScroll()
    {
        if (mUseScroller)
        {
            if (mScroller.computeScrollOffset())
            {
                if (mItemScrollStateListener != null && !mIsScrollStart)
                {
                    mItemScrollStateListener.onScrollStart();
                    mIsScrollStart = true;
                }
                scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
                postInvalidate();
            }
            else
            {
                int lastSelIndex = mSelIndex;

                if (mTempIndex == mTargetIndex)
                {
                    mUseScroller = false;
                    mUIEnable = true;

                    mSelIndex = mTargetIndex;
                    mTempIndex = -1;
                    mTargetIndex = -1;

                    int count = getChildCount();

                    if (mItemScrollStateListener != null && mSelIndex != lastSelIndex && count > 1)
                    {
                        if (mAdapter != null)
                        {
                            TabInfo info = mAdapter.getTabInfoByIndex(mSelIndex);
                            int type = (int) info.getTag();
                            mItemScrollStateListener.onItemSelected(mSelIndex, type);

                            for (int i = 0; i < count; i++)
                            {
                                View child = getChildAt(i);
                                if (child != null)
                                {
                                    mAdapter.onBindView(child, i);
                                }
                            }
                        }
                    }

                    if (mItemScrollStateListener != null && mSelIndex == lastSelIndex)
                    {
                        mItemScrollStateListener.onScrollEnd();
                    }

                    mIsScrollStart = false;
                }
                else
                {
                    mSelIndex = mTempIndex;
                    SmoothToPosition(mTargetIndex);
                }
            }
        }
        else if (mIsScrollTo)
        {
            mIsScrollTo = false;
            mUIEnable = true;
            if (mAdapter != null)
            {
                int count = getChildCount();
                for (int i = 0; i < count; i++)
                {
                    View child = getChildAt(i);
                    if (child != null)
                    {
                        mAdapter.onBindView(child, i);
                    }
                }
            }
        }
    }

    public void updateChildrenUI()
    {
        int count = getChildCount();
        if (mAdapter != null)
        {
            for (int i = 0; i < count; i++)
            {
                View child = getChildAt(i);
                if (child != null)
                {
                    mAdapter.onBindView(child, i);
                }
            }
        }
    }

    public int GetCurrSelIndex()
    {
        return mSelIndex;
    }

    public boolean isScrollStarted()
    {
        return mIsScrollStart;
    }

    public void SetOriginallySelectedIndex(int index)
    {
        mInitSelIndex = index;
    }

    private boolean mIsScrollTo = false;

    public void SelectCurrentIndex(int index)
    {
        if (index < 0 || index >= getChildCount())
        {
            return;
        }

        if (mFinishMeasure && getVisibility() == VISIBLE
                && ((ViewGroup) getParent()).getVisibility() == VISIBLE
                && ((ViewGroup) getParent().getParent()).getVisibility() == VISIBLE)
        {
            if (mUIEnable && mSelIndex != index)
            {
                int loc = getViewLocation(getChildAt(index));
                int dx = loc - mSelAreaMidX;
                mSelIndex = index;
                scrollBy(dx, 0);
                mIsScrollTo = true;
            }
        }
        else
        {
            mTargetIndex = index;
            getViewTreeObserver().addOnPreDrawListener(mPreDrawListener);
        }
    }

    private ViewTreeObserver.OnPreDrawListener mPreDrawListener = new ViewTreeObserver.OnPreDrawListener()
    {
        @Override
        public boolean onPreDraw()
        {
            getViewTreeObserver().removeOnPreDrawListener(this);
            int index = mTargetIndex;
            mTargetIndex = -1;
            SelectCurrentIndex(index);
            return true;
        }
    };

    public void SmoothToPosition(int index)
    {
        if (mUIEnable && mSelIndex != index)
        {
            int size = getChildCount();
            if (size > 0 && index >= 0 && index < size)
            {
                mUseScroller = true;
                mUIEnable = false;

                if (mIsMoreThan18SDK)
                {
                    AutoSelItem(index);
                }
                else if (mAdapter != null)
                {
                    TabInfo info = mAdapter.getTabInfoByIndex(index);
                    if (info != null)
                    {
                        int type = (int) info.getTag();

                        switch (type)
                        {
                            case ShutterConfig.TabType.GIF:
                            case ShutterConfig.TabType.VIDEO:
                            {
                                resetPosition();
                                if (mItemScrollStateListener != null)
                                {
                                    mItemScrollStateListener.onLessThan18SDK(type);
                                }
                                break;
                            }

                            case ShutterConfig.TabType.CUTE:
                            case ShutterConfig.TabType.PHOTO:
                            {
                                AutoSelItem(index);
                                break;
                            }
                        }
                    }
                }
                invalidate();
            }
        }
    }

    public PointF getStickerLogoCenter()
    {
        if (mAdapter == null) return null;

        View view = getChildAt(2);
        int[] loc = new int[2];
        view.getLocationOnScreen(loc);

        int sumW = getChildAt(2).getMeasuredWidth() + getChildAt(3).getMeasuredWidth();

        return new PointF(loc[0] + sumW / 2, loc[1]);
    }

    /**
     * Up事件必须调用这个方法
     */
    private void AutoSelItem(int index)
    {
        mUseScroller = true;
        mUIEnable = false;

        int loc = getViewLocation(getChildAt(index));
        int dx = loc - mSelAreaMidX;

        mScroller.startScroll(getScrollX(), 0, dx, 0);

        mTargetIndex = index;
        mTempIndex = index;

        invalidate();
    }

    private void resetPosition()
    {
        mUseScroller = false;
        mIsScrollTo = true;
        mUIEnable = false;

        int loc = getViewLocation(getChildAt(mSelIndex));
        int dx = loc - mSelAreaMidX;

        scrollBy(dx, 0);
        invalidate();
    }

    private boolean canScrollChildren()
    {
        return getChildCount() > 1;
    }

    private TabViewBaseAdapter mAdapter;

    public TabViewBaseAdapter getAdapter()
    {
        return mAdapter;
    }

    public void setAdapter(@NonNull TabViewBaseAdapter adapter)
    {
        mAdapter = adapter;
        adapter.setView(this);

        int itemCount = adapter.getItemCount();

        for (int i = 0; i < itemCount; i++)
        {
            int itemType = adapter.getItemViewType(i);
            View child = adapter.onCreateView(this, itemType, i);
            if (child != null)
            {
                this.addView(child);
            }
        }
    }
}
