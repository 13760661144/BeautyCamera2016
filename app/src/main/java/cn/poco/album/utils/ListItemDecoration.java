package cn.poco.album.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by: fwc
 * Date: 2016/12/16
 */
public class ListItemDecoration extends RecyclerView.ItemDecoration {

	public static final int VERTICAL = 0;
	public static final int HORIZONTAL = 1;

	private int mDivideSpace;

	private int mDirection;

	public ListItemDecoration(int divideSpace, int direction) {
		mDivideSpace = divideSpace;
		mDirection = direction;
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

		int divide = mDivideSpace;
		int childPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
		if (childPosition == 0) {
			divide = 0;
		}

		if (mDirection == VERTICAL) {
			outRect.set(0, divide, 0, 0);
		} else if (mDirection == HORIZONTAL) {
			outRect.set(divide, 0, 0, 0);
		}
	}
}
