package cn.poco.video.view;

import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * Created by lgd on 2017/6/21.
 */

public class ClipHorizontalScrollView extends HorizontalScrollView
{

    private static final String TAG = "ClipHorizontalScrollView";

    private Handler mHandler;

    private ScrollViewListener mScrollViewListener;

    /**
     * 滚动状态:
     * IDLE         = 滚动停止
     * TOUCH_SCROLL = 手指拖动滚动
     * FLING        = 滚动
     */
    public enum ScrollType
    {
        IDLE, TOUCH_SCROLL, FLING
    }

    /**
     * 记录当前滚动的距离
     */
    private int currentX = 0;

    /**
     * 当前滚动状态
     */
    private ScrollType scrollType = ScrollType.IDLE;

    public ClipHorizontalScrollView(Context context)
    {
        super(context);
        mHandler = new Handler();
    }

    /**
     * 滚动监听runnable
     */
    private Runnable scrollRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            if (getScrollX() == currentX)
            {
                //滚动停止,取消监听线程
                scrollType = ScrollType.IDLE;
                if (mScrollViewListener != null)
                {
                    mScrollViewListener.onScrollChanged(scrollType, getScrollX());
                }
                mHandler.removeCallbacks(this);
                return;
            }
            else
            {
                //手指离开屏幕,但是view还在滚动
                scrollType = ScrollType.FLING;
                if (mScrollViewListener != null)
                {
                    mScrollViewListener.onScrollChanged(scrollType, getScrollX());
                }
            }
            currentX = getScrollX();
            //滚动监听间隔:milliseconds
            mHandler.postDelayed(this, 10);
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                this.scrollType = ScrollType.TOUCH_SCROLL;
                if(mScrollViewListener != null)
                {
                    mScrollViewListener.onScrollChanged(scrollType, getScrollX());
                }
                mHandler.removeCallbacks(scrollRunnable);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                checkScroll();
                break;
        }
        return super.onTouchEvent(ev);
    }

    protected void checkScroll()
    {
        mHandler.post(scrollRunnable);
    }


    public void setScrollViewListener(ScrollViewListener mScrollViewListener)
    {
        this.mScrollViewListener = mScrollViewListener;
    }

    public interface ScrollViewListener
    {
        void onScrollChanged(ScrollType scrollType, int scrollX);
    }
}