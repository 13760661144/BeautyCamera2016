package cn.poco.widget.recycle;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import cn.poco.recycleview.AbsDragAdapter;
import cn.poco.recycleview.DragRecycleViewContainer;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by lgd on 2017/5/15.
 * 该布局包含recycleView，  拖拽时使用真的item拖拽
 */

public class RecommendDragContainer extends DragRecycleViewContainer {
    private ImageView delete;
    private FrameLayout deletePage;
    private boolean m_uiEnable = true;
    private boolean isCanDelete = true;

    public RecommendDragContainer(@NonNull Context context, AbsDragAdapter adapter) {
        super(context, adapter);
        rect = new Rect(ShareData.m_screenWidth / 2 - ShareData.PxToDpi_xhdpi(100), ShareData.m_screenHeight / 2 - ShareData.PxToDpi_xhdpi(100), ShareData.m_screenWidth / 2 + ShareData.PxToDpi_xhdpi(100), ShareData.m_screenHeight / 2 + ShareData.PxToDpi_xhdpi(100));

    }

    @Override
    protected void initUi(RecyclerView recyclerView) {
        initDeleteUi();
        LayoutParams params;
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(232));
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        addView(recyclerView, params);
    }

    protected void initDeleteUi() {
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

    @Override
    public void onDragMove(View dragView) {
        super.onDragMove(dragView);
    }

    @Override
    public void isInDeleteRect(boolean b) {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!m_uiEnable) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setUIEnable(boolean flag) {
        m_uiEnable = flag;
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
