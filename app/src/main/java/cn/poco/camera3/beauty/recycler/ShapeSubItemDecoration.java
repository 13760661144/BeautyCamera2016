package cn.poco.camera3.beauty.recycler;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author lmx
 *         Created by lmx on 2017-12-11.
 */

public class ShapeSubItemDecoration extends RecyclerView.ItemDecoration
{
    public ShapeSubItemDecoration()
    {
    }

    public int firstItemDivide;
    public int lastItemDivide;
    public int defaultItemDivide;

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
    {
        int childAdapterPosition = parent.getChildAdapterPosition(view);
        int itemCount = parent.getAdapter().getItemCount();
        if (childAdapterPosition == 0)
        {
            outRect.set(firstItemDivide, 0, 0, 0);
        }
        else if (childAdapterPosition == itemCount - 1)
        {
            outRect.set(defaultItemDivide, 0, lastItemDivide, 0);
        }
        else
        {
            outRect.set(defaultItemDivide, 0, 0, 0);
        }
    }
}
