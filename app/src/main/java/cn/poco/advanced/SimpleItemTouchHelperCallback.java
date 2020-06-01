package cn.poco.advanced;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * @author lmx
 *         Created by lmx on 2017/3/14.
 */

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private ItemTouchHelperAdapter mItemHelperAdapter;

    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter mItemHelperAdapter) {
        this.mItemHelperAdapter = mItemHelperAdapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            int orientation = linearLayoutManager.getOrientation();

            int dragFlags = 0;
            int swipeFlags = 0;

            if (orientation == LinearLayoutManager.HORIZONTAL) {//横向布局
                dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                swipeFlags = 0/*ItemTouchHelper.UP | ItemTouchHelper.DOWN*/;
            } else if (orientation == LinearLayoutManager.VERTICAL) {//纵向布局
                dragFlags = 0/*ItemTouchHelper.UP | ItemTouchHelper.DOWN*/;
                swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            }
            return makeMovementFlags(dragFlags, swipeFlags);
        }
        return 0;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        if (mItemHelperAdapter != null) {
            mItemHelperAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        }
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (mItemHelperAdapter != null) {
            mItemHelperAdapter.onItemDismiss(viewHolder.getAdapterPosition());
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        ((ItemTouchHelperViewHolder) viewHolder).onItemClear();
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            ((ItemTouchHelperViewHolder) viewHolder).onItemSelected();
        }
        super.onSelectedChanged(viewHolder, actionState);
    }
}
