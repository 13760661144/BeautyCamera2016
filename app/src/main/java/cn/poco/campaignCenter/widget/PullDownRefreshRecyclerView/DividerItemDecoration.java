package cn.poco.campaignCenter.widget.PullDownRefreshRecyclerView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import cn.poco.campaignCenter.ui.cells.CampaignCell;
import cn.poco.tianutils.ShareData;


/**
 * Created by Shine on 2016/12/15.
 */

    public class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private static final int[] ATTRS = new int[]{
                android.R.attr.listDivider
        };

        public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

        public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

        private ShapeDrawable mDivider;
        private int mOrientation;

        public DividerItemDecoration(Context context, int orientation) {
            mDivider = new ShapeDrawable();
            mDivider.setIntrinsicHeight(ShareData.PxToDpi_xhdpi(10));
            mDivider.setAlpha(0);
            setOrientation(orientation);
        }

        public void setOrientation(int orientation) {
            if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
                throw new IllegalArgumentException("invalid orientation");
            }
            mOrientation = orientation;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            if (mOrientation == VERTICAL_LIST) {
                drawVertical(c, parent);
            } else {
                drawHorizontal(c, parent);
            }
        }

        public void drawVertical(Canvas c, RecyclerView parent) {
            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                int viewIndex = parent.getLayoutManager().getPosition(child);
                int totalCount = parent.getAdapter().getItemCount();
                if (child instanceof CampaignCell && viewIndex != 1 && viewIndex != totalCount - 3) {
                    final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                            .getLayoutParams();
                    final int top = child.getBottom() + params.bottomMargin;
                    final int bottom = top + mDivider.getIntrinsicHeight();
                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(c);
                }
            }
        }

        public void drawHorizontal(Canvas c, RecyclerView parent) {
            final int top = parent.getPaddingTop();
            final int bottom = parent.getHeight() - parent.getPaddingBottom();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int left = child.getRight() + params.rightMargin;
                final int right = left + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == VERTICAL_LIST) {
            int viewIndex = parent.getLayoutManager().getPosition(view);
            int totalCount = parent.getAdapter().getItemCount();
            if (view instanceof CampaignCell && viewIndex != 1 && viewIndex != totalCount - 3) {
                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            }
        } else {
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
        }
    }
}
