package cn.poco.water;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 水印item间距
 * Created by Gxx on 2017/12/13.
 */

public class WatermarkDecoration extends RecyclerView.ItemDecoration
{
	private ColorDrawable mDrawable;
	private Rect mBounds;
	private int mDrawableArea; // 间隔的范围

	WatermarkDecoration()
	{
		mDrawable = new ColorDrawable(0x0DFFFFFF);
		mDrawableArea = 1;
		mBounds = new Rect();
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
	{
		super.getItemOffsets(outRect, view, parent, state);

		// 必须要用adapter get size
		// layoutManager \ parent get 到的 item count 是动态的
		int position = parent.getChildAdapterPosition(view);
		GridLayoutManager layoutManager = (GridLayoutManager)parent.getLayoutManager();
		int span = layoutManager.getSpanCount();

		if(position != 0)
		{
			if(position >= span)
			{
				outRect.top = mDrawableArea;
			}

			if(position % span != 0)
			{
				outRect.left = mDrawableArea;
			}
		}
	}

	private void drawVertical(Canvas canvas, RecyclerView parent)
	{
		canvas.save();

		RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();

		int left;
		int top;
		int right;
		int bottom;

		if(layoutManager instanceof GridLayoutManager)
		{
			int span = ((GridLayoutManager)layoutManager).getSpanCount();

			final int childCount = parent.getChildCount();
			for(int i = 0; i < childCount; i++)
			{
				View child = parent.getChildAt(i);
				parent.getDecoratedBoundsWithMargins(child, mBounds);
				if(i > 0)
				{
					if(i % span != 0)
					{
						left = mBounds.left;
						right = left + mDrawableArea;
					}
					else
					{
						right = left = mBounds.left;
					}
					top = mBounds.top;
					bottom = mBounds.bottom;
					mDrawable.setBounds(left, top, right, bottom);
					mDrawable.draw(canvas);
				}
			}
		}

		canvas.restore();
	}

	private void drawHorizontal(Canvas canvas, RecyclerView parent)
	{
		canvas.save();

		RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();

		int left;
		int right;
		int top;
		int bottom;

		if(layoutManager instanceof GridLayoutManager)
		{
			int span = ((GridLayoutManager)layoutManager).getSpanCount();

			final int childCount = parent.getChildCount();
			for(int i = 0; i < childCount; i++)
			{
				View child = parent.getChildAt(i);
				parent.getDecoratedBoundsWithMargins(child, mBounds);
				if(i >= span)
				{
					left = mBounds.left;
					right = mBounds.right;

					top = mBounds.top;
					bottom = top + mDrawableArea;
					mDrawable.setBounds(left, top, right, bottom);
					mDrawable.draw(canvas);
				}
			}
		}

		canvas.restore();
	}

	@Override
	public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state)
	{
		super.onDraw(c, parent, state);
		drawHorizontal(c, parent);
		drawVertical(c, parent);
	}
}
