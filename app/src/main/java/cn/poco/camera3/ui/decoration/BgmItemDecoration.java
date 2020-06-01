package cn.poco.camera3.ui.decoration;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import cn.poco.home.home4.utils.PercentUtil;

/**
 * 预览背景音乐
 * Created by Gxx on 2017/10/16.
 */

public class BgmItemDecoration extends RecyclerView.ItemDecoration
{
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
    {
        RecyclerView.LayoutManager mgr = parent.getLayoutManager();
        if (mgr instanceof LinearLayoutManager)
        {
            outRect.left = PercentUtil.WidthPxToPercent(30);
            int position = parent.getChildAdapterPosition(view);
            int size = parent.getAdapter().getItemCount();
            if (position == 0)
            {
                outRect.left = PercentUtil.WidthPxToPercent(48);
            }

            if (position == size - 1)
            {
                outRect.right = PercentUtil.WidthPxToPercent(48);
            }
        }
    }
}
