package cn.poco.dynamicSticker;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import cn.poco.dynamicSticker.helper.ItemTouchHelperAdapter;
import cn.poco.dynamicSticker.helper.SimpleItemTouchHelperCallback;

/**
 * Created by zwq on 2017/03/08 16:16.<br/><br/>
 */

public class MyItemTouchHelperCallback extends SimpleItemTouchHelperCallback {

    public interface OnItemDragListener {
        public static final int IDLE = -1;
        public static final int START = 0;
        public static final int DRAGGING = 1;
        public static final int STOP = 2;

        /**
         *
         * @param viewHolder
         * @param dragState  拖动状态
         * @param x  相对于屏幕的位置
         * @param y  相对于屏幕的位置
         */
        public void onItemDrag(RecyclerView.ViewHolder viewHolder, int dragState, float x, float y);

    }

    private OnItemDragListener mOnItemDragListener;
    private int mDragState = OnItemDragListener.IDLE;
    private float mSrcX, mSrcY;

    public MyItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        super(adapter);
    }

    public void setOnItemDragListener(OnItemDragListener listener) {
        mOnItemDragListener = listener;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        final int swipeFlags = 0;//ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
//        return super.onMove(recyclerView, source, target);
        return false;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        if (mOnItemDragListener == null) {
            return;
        }
        if (dX == 0 && dY == 0 && mDragState == OnItemDragListener.IDLE) {
            mDragState = OnItemDragListener.START;
        }
        if (mDragState == OnItemDragListener.IDLE) {
            return;
        }
        if (mDragState == OnItemDragListener.START) {
            int[] mViewLoc = new int[2];
            viewHolder.itemView.getLocationOnScreen(mViewLoc);
            mSrcX = mViewLoc[0];
            mSrcY = mViewLoc[1];
        }
        mOnItemDragListener.onItemDrag(viewHolder, mDragState, mSrcX + dX, mSrcY + dY);

        if (mDragState == OnItemDragListener.START) {
            mDragState = OnItemDragListener.DRAGGING;
        } else if (mDragState == OnItemDragListener.STOP) {
            mDragState = OnItemDragListener.IDLE;
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (mDragState == OnItemDragListener.DRAGGING) {
            mDragState = OnItemDragListener.STOP;
        }
    }
}
