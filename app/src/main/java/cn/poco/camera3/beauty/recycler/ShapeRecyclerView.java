package cn.poco.camera3.beauty.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

/**
 * @author lmx
 *         Created by lmx on 2017-12-11.
 */

public class ShapeRecyclerView extends RecyclerView
{
    public ShapeRecyclerView(Context context)
    {
        super(context);
    }

    private boolean mUiEnable = true;

    public void setUiEnable(boolean mUiEnable)
    {
        this.mUiEnable = mUiEnable;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        if (!this.mUiEnable) return false;
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        return super.onTouchEvent(e);
    }
}
