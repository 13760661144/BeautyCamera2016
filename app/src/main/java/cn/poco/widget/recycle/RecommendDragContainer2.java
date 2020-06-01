package cn.poco.widget.recycle;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import cn.poco.recycleview.DragRecycleView;
import cn.poco.recycleview.DragRecycleViewContainer2;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by lgd on 2017/5/15.
 * 该布局不包含recycleView，只持有引用，  拖拽时使用假的item拖拽
 */

public class RecommendDragContainer2 extends DragRecycleViewContainer2 {
    private ImageView delete;
    private FrameLayout deletePage;
    private boolean isCanDelete = true;

    public RecommendDragContainer2(@NonNull Context context, DragRecycleView dragRecycleView) {
        super(context, dragRecycleView);
        rect = new Rect(ShareData.m_screenWidth / 2 - ShareData.PxToDpi_xhdpi(100), ShareData.m_screenHeight / 2 - ShareData.PxToDpi_xhdpi(100), ShareData.m_screenWidth / 2 + ShareData.PxToDpi_xhdpi(100), ShareData.m_screenHeight / 2 + ShareData.PxToDpi_xhdpi(100));
    }

    @Override
    protected void init() {
        LayoutParams params;
        deletePage = new FrameLayout(getContext());
        deletePage.setVisibility(View.GONE);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(deletePage, params);

        View view = new View(getContext());
        params = new LayoutParams(ShareData.PxToDpi_xhdpi(100), ShareData.PxToDpi_xhdpi(100));
        params.gravity = Gravity.CENTER;
        view.setBackgroundColor(Color.GRAY);
        deletePage.addView(view, params);

        delete = new ImageView(getContext());
        delete.setImageResource(R.drawable.cloudalbum_delete);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        deletePage.addView(delete, params);
        super.init();
    }

    public RecyclerView getRecyclerView() {
        return mDragRecycleView;
    }

    private Rect rect;

    @Override
    public Rect getDeleteRect() {
        return rect;
    }

    @Override
    public void isInDeleteRect(boolean b) {

    }


    @Override
    public void onDragStart(View dragView) {
        super.onDragStart(dragView);
        if (isCanDelete) {
            deletePage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDragEnd(View dragView, boolean isDelete) {
        super.onDragEnd(dragView, isDelete);
        if (isCanDelete) {
            deletePage.setVisibility(View.GONE);
        }
    }


    public void setCanDelete(boolean canDelete) {
        isCanDelete = canDelete;
        if (isCanDelete) {
            rect = new Rect(ShareData.m_screenWidth / 2 - ShareData.PxToDpi_xhdpi(100), ShareData.m_screenHeight / 2 - ShareData.PxToDpi_xhdpi(100), ShareData.m_screenWidth / 2 + ShareData.PxToDpi_xhdpi(100), ShareData.m_screenHeight / 2 + ShareData.PxToDpi_xhdpi(100));
        } else {
            rect = new Rect(0, 0, 0, 0);
        }
    }
}
