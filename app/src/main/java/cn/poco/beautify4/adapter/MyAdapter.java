package cn.poco.beautify4.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;

import cn.poco.beautify4.UiMode;
import cn.poco.beautify4.view.MyButton1;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;

/**
 * Created by Raining on 2017/2/4.
 * 用于美化底下bar的RecyclerView
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>
{
	protected ArrayList<MyItem> mData;
	protected MyListener mListener;

	public void setData(ArrayList<MyItem> arr)
	{
		mData = arr;
	}

	public void setCallback(MyListener listener)
	{
		mListener = listener;
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		MyButton1 btn = new MyButton1(parent.getContext());
		btn.setOnTouchListener(new OnAnimationClickListener()
		{
			@Override
			public void onAnimationClick(View v)
			{
				if(mListener != null && v instanceof MyButton1)
				{
					mListener.onClick(((MyButton1)v).getRes());
				}
			}

			@Override
			public void onTouch(View v)
			{

			}

			@Override
			public void onRelease(View v)
			{

			}
		});
		btn.setOnLongClickListener(new View.OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View v)
			{
				if(mListener != null && v instanceof MyButton1 && mListener.isLongPressDragEnabled())
				{
					mListener.onLongClick((MyButton1)v);
					return true;
				}
				return false;
			}
		});
		return new MyViewHolder(btn);
	}

	@Override
	public void onBindViewHolder(MyViewHolder holder, int position)
	{
		MyItem item = mData.get(position);
		holder.mBtn.SetData((Integer)item.mIcon, item.mName);
		if (item.mMode == UiMode.YIJIANMENGZHUANG) {
			holder.mBtn.SetNew(item.mShowNew, ShareData.PxToDpi_xhdpi(12), 0);
		} else {
			holder.mBtn.SetNew(item.mShowNew, 0, 0);
		}
	}

	@Override
	public int getItemCount()
	{
		return mData.size();
	}

	public static class MyViewHolder extends RecyclerView.ViewHolder
	{
		public MyButton1 mBtn;

		public MyViewHolder(View itemView)
		{
			super(itemView);
			mBtn = (MyButton1)itemView;
		}
	}

	public static class MyItem
	{
		public Object mIcon;
		public String mName;
		public boolean mShowNew;
		public UiMode mMode;

		public MyItem(UiMode mode, Object icon, String name, boolean showNew)
		{
			mMode = mode;
			mIcon = icon;
			mName = name;
			mShowNew = showNew;
		}
	}

	public static int getIndex(ArrayList<MyAdapter.MyItem> arr, int res)
	{
		int out = -1;

		if(arr != null)
		{
			int i = 0;
			for(MyItem item : arr)
			{
				if(((Integer)item.mIcon) == res)
				{
					out = i;
					break;
				}
				i++;
			}
		}

		return out;
	}

	public interface MyListener
	{
		void onClick(int res);

		void onLongClick(MyButton1 btn);

		boolean isLongPressDragEnabled();

		void onMove();

		void onSwiped();
	}

	public static class DefaultItemTouchHelper extends ItemTouchHelper
	{
		public DefaultItemTouchHelper(final MyAdapter adapter)
		{
			super(new ItemTouchHelper.Callback()
			{
				@Override
				public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder)
				{
					RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
					if(layoutManager instanceof LinearLayoutManager)
					{
						LinearLayoutManager linearLayoutManager = (LinearLayoutManager)layoutManager;
						int orientation = linearLayoutManager.getOrientation();

						int dragFlag = 0;
						int swipeFlag = 0;

						// 为了方便理解，相当于分为横着的ListView和竖着的ListView
						if(orientation == LinearLayoutManager.HORIZONTAL)
						{// 如果是横向的布局
							swipeFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
							dragFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
						}
						else if(orientation == LinearLayoutManager.VERTICAL)
						{// 如果是竖向的布局，相当于ListView
							dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
							swipeFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
						}
						return makeMovementFlags(dragFlag, swipeFlag);
					}
					return 0;
				}

				@Override
				public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target)
				{
					if(adapter != null && adapter.mData != null)
					{
						int src = viewHolder.getAdapterPosition();
						int dst = target.getAdapterPosition();
						Collections.swap(adapter.mData, src, dst);
						adapter.notifyItemMoved(src, dst);
						if(adapter.mListener != null)
						{
							adapter.mListener.onMove();
						}
						return true;
					}
					return false;
				}

				@Override
				public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
				{
					if(adapter != null && adapter.mData != null)
					{
						int pos = viewHolder.getAdapterPosition();
						adapter.mData.remove(pos);
						adapter.notifyItemRemoved(pos);
						if(adapter.mListener != null)
						{
							adapter.mListener.onSwiped();
						}
					}
				}

				@Override
				public boolean isLongPressDragEnabled()
				{
					boolean out = false;
					if(adapter != null && adapter.mListener != null)
					{
						out = adapter.mListener.isLongPressDragEnabled();
					}
					return out;
				}

				@Override
				public boolean isItemViewSwipeEnabled()
				{
					return false;
				}
			});
		}
	}
}
