package cn.poco.camera3.ui.decoration;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import cn.poco.tianutils.ShareData;

/**
 * 贴纸素材
 * Created by Gxx on 2017/10/16.
 */

public class StickerItemDecoration extends RecyclerView.ItemDecoration
{
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
    {
        int count = parent.getAdapter().getItemCount();
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager)
        {
            int spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
            int remainder = count % spanCount;
            int pos = parent.getChildAdapterPosition(view);

            if (pos >= 0 && pos < spanCount) // 第一列
            {
                outRect.top = ShareData.PxToDpi_xhdpi(10);
            }
            else
            {
                outRect.top = ShareData.PxToDpi_xhdpi(6);
            }

            outRect.left = ShareData.PxToDpi_xhdpi(30) - (pos % spanCount) * ShareData.PxToDpi_xhdpi(6);

            if (remainder == 0)
            {
                if (pos >= count - spanCount && pos < count)// 最后一行
                {
                    outRect.bottom = ShareData.PxToDpi_xhdpi(130);
                }
            }
            else
            {
                if (pos >= count - remainder && pos < count)// 最后一行
                {
                    outRect.bottom = ShareData.PxToDpi_xhdpi(130);
                }
            }

        }
    }
}
