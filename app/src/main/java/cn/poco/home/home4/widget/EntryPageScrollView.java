package cn.poco.home.home4.widget;

import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * Created by admin on 2017/12/15.
 */

public class EntryPageScrollView extends HorizontalScrollView
{
    //    private LinearLayout mContainer;
//    private boolean isRight;
    private String TAG = "MyScrollView ";
    private boolean isLeftMode = true;
    private int maxScrollX;
    private Handler mHandler;
    private ScrollListener mScrollListener;


    public EntryPageScrollView(Context context)
    {
        super(context);
        mHandler = new Handler();
    }

    public int setMaxScroll(int maxScrollX)
    {
        this.maxScrollX = maxScrollX;
        return maxScrollX;
    }
    private boolean isFling = false;
    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        //看HorizontalScrollView源码  super.onTouchEvent(ev)可能执行fling
        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if(mScrollListener != null)
                {
                    mScrollListener.onScrollChanged(getScrollX());
                }
                mHandler.removeCallbacks(scrollRunnable);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mHandler.post(scrollRunnable);
                break;
        }
        boolean rel = super.onTouchEvent(ev);
        switch (ev.getAction())
        {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //如果没有执行fling才根据速距离判断
                if(!isFling)
                {
                    if (getScrollX() > maxScrollX / 3 && isLeftMode)
                    {
                        isLeftMode = false;
                        this.smoothScrollTo(maxScrollX, 0);
                    } else if (getScrollX() < maxScrollX * 2 / 3 && !isLeftMode)
                    {
                        isLeftMode = true;
                        this.smoothScrollTo(0, 0);
                    }else{
                        if(isLeftMode){
                            this.smoothScrollTo(0, 0);
                        }else{
                            this.smoothScrollTo(maxScrollX, 0);
                        }
                    }
                    mHandler.post(scrollRunnable);
                }
                isFling = false;
                break;
        }
        return rel;
    }

    @Override
    public void fling(int velocityX)
    {
        if (getChildCount() > 0)
        {
            if (velocityX > 1000)
            {
                isFling = true;
                isLeftMode = false;
                this.smoothScrollTo(maxScrollX, 0);
                mHandler.post(scrollRunnable);
            } else if (velocityX < -1000)
            {
                isFling = true;
                isLeftMode = true;
                this.smoothScrollTo(0, 0);
                mHandler.post(scrollRunnable);
            }
        }
    }

    private Runnable scrollRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            if (mScrollListener != null)
            {
                mScrollListener.onScrollChanged(getScrollX());
            }
            if (getScrollX() == 0 || getScrollX() == maxScrollX )
            {
                //滚动停止,取消监听线程
                mHandler.removeCallbacks(this);
                return;
            }
            //滚动监听间隔:milliseconds
            mHandler.postDelayed(this, 16);
        }
    };

    public void setScrollListener(ScrollListener scrollListener)
    {
        this.mScrollListener = scrollListener;
    }

    public interface ScrollListener
    {
        void onScrollChanged(int scrollX);
    }

}

