package cn.poco.camera3.ui.decoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import cn.poco.home.home4.utils.PercentUtil;

/**
 * 美形定制-调整窗口
 * Created by Gxx on 2017/10/16.
 */

public class AdjustItemDecoration extends RecyclerView.ItemDecoration
{
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
    {
        // 必须要用adapter get size
        int size = parent.getAdapter().getItemCount();
        // layoutManager \ parent get 到的 item count 是动态的
        int position = parent.getChildAdapterPosition(view);

        outRect.left = PercentUtil.WidthPxToPercent(60);

        if (position == 0)
        {
            outRect.left = PercentUtil.WidthPxToPercent(55);
        }
        else if(position == size - 1)
        {
            outRect.right = PercentUtil.WidthPxToPercent(55);
        }
    }
}
