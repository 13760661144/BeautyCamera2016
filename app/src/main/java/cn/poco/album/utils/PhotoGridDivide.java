package cn.poco.album.utils;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by: fwc
 * Date: 2016/11/22
 */
public class PhotoGridDivide extends RecyclerView.ItemDecoration {

	private int mHorizontalSpace;
	private int mVerticalSpace;
	private boolean mHasHeader;

	public PhotoGridDivide(int horizontalSpace, int verticalSpace, boolean hasHeader) {
		mHorizontalSpace = horizontalSpace;
		mVerticalSpace = verticalSpace;
		mHasHeader = hasHeader;
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

		GridLayoutManager manager = (GridLayoutManager)parent.getLayoutManager();
		int spanCount = manager.getSpanCount();
		int childPosition = ((RecyclerView.LayoutParams)view.getLayoutParams()).getViewLayoutPosition();
		if (mHasHeader) {
			if (childPosition == 0) {
				outRect.set(0, 0, 0, 0);
				return;
			} else {
				childPosition--;
			}
		}

		int childCount = parent.getAdapter().getItemCount();

		int unit = (int)(mHorizontalSpace / 3.0f + 0.5f);

		int verticalSpace = mVerticalSpace;
		if (mHorizontalSpace == mVerticalSpace) {
			// 确保横竖分割线相等
			verticalSpace = unit * 3;
		}

		int left, right;

		switch ((childPosition + 1) % spanCount) {
			case 0:
				// 最后一列
				left = 2 * unit;
				right = 0;
				break;
			case 1:
				// 第一列
				left = 0;
				right = 2 * unit;
				break;
			default:
				left = right = unit;
				break;
		}

		int spanLeft = childCount % spanCount;
		if (spanLeft == 0) {
			spanLeft = spanCount;
		}
		childCount = childCount - spanLeft;
		if (childPosition >= childCount) {
			// 最后一行，则不需要绘制底部
			verticalSpace = 0;
		}

		outRect.set(left, 0, right, verticalSpace);

	}
}
