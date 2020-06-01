package cn.poco.pendant.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

/**
 * Created by: fwc
 * Date: 2017/2/22
 */
public class MyRecyclerView extends RecyclerView {

	private float mLast = -1;

	private OnMyScrollListener mOnMyScrollListener;

	private LinearLayoutManager mLayoutManager;

	public MyRecyclerView(Context context) {
		super(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {

		switch (e.getAction()) {
			case MotionEvent.ACTION_DOWN:
				// DOWN事件被子View消耗了，不会触发
				mLast = e.getX();
				break;
			case MotionEvent.ACTION_MOVE:
				if (mLayoutManager == null) {
					mLayoutManager = (LinearLayoutManager) getLayoutManager();
				}

				if (mLast == -1) {
					mLast = e.getX();
				} else {
					int position = mLayoutManager.findFirstCompletelyVisibleItemPosition();

					float move = e.getX() - mLast;
					if (mOnMyScrollListener != null && position > 0) {
						mOnMyScrollListener.onScroll(move);
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				mLast = -1;
				break;
		}
		return super.onTouchEvent(e);
	}

	public void setOnMyScrollListener(OnMyScrollListener myScrollListener) {
		mOnMyScrollListener = myScrollListener;
	}

	public interface OnMyScrollListener {
		void onScroll(float dx);
	}
}
